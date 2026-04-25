import re
from collections.abc import Sequence
from typing import cast

import litellm
from litellm.types.utils import ModelResponse
from openai import BadRequestError, UnprocessableEntityError

litellm.drop_params = True

# Workaround for litellm bug: OpenRouter's transform_request() unconditionally injects
# `usage: {"include": True}` as a top-level request parameter. The OpenAI SDK rejects
# this with TypeError since `usage` is not a valid kwarg for chat.completions.create().
# We patch transform_request to move `usage` into `extra_body` where the OpenAI SDK
# passes it through as an extra JSON body field — which is what OpenRouter expects.
try:
    from litellm.llms.openrouter.chat.transformation import OpenrouterConfig

    _original_transform_request = OpenrouterConfig.transform_request

    def _patched_transform_request(self, *args, **kwargs):  # type: ignore[no-untyped-def]
        data = _original_transform_request(self, *args, **kwargs)
        data.pop("usage", None)
        return data

    OpenrouterConfig.transform_request = _patched_transform_request  # type: ignore[assignment]
except (ImportError, AttributeError):
    pass  # litellm version without this issue
from openai.types.chat import ChatCompletionMessage
from tenacity import retry, retry_if_not_exception_type, stop_after_attempt, wait_random_exponential

from agentdojo.agent_pipeline.base_pipeline_element import BasePipelineElement
from agentdojo.agent_pipeline.llms.openai_llm import (
    _function_to_openai,
    _message_to_openai,
    _openai_to_assistant_message,
)
from agentdojo.functions_runtime import EmptyEnv, Env, FunctionsRuntime
from agentdojo.types import (
    ChatMessage,
    ChatUserMessage,
    get_text_content_as_str,
    text_content_block_from_string,
)

_THINKING_PATTERN = re.compile(r"^(.+)-thinking-(\d+)$")
_REASONING_PATTERN = re.compile(r"^(.+)-reasoning-(low|medium|high|xhigh)$")


@retry(
    wait=wait_random_exponential(multiplier=1, max=40),
    stop=stop_after_attempt(3),
    reraise=True,
    retry=retry_if_not_exception_type((BadRequestError, UnprocessableEntityError)),
)
def _litellm_completion(
    model: str,
    messages: list,
    tools: list,
    temperature: float,
    thinking_budget: int | None,
    reasoning_effort: str | None,
    extra_kwargs: dict,
):
    kwargs: dict = {
        "model": model,
        "messages": messages,
        "tools": tools or None,
        "tool_choice": "auto" if tools else None,
        "temperature": temperature,
        **extra_kwargs,
    }
    if thinking_budget is not None:
        kwargs["thinking"] = {"type": "enabled", "budget_tokens": thinking_budget}
        kwargs["temperature"] = 1.0
    if reasoning_effort is not None:
        kwargs["reasoning_effort"] = reasoning_effort
    return litellm.completion(**kwargs)  # type: ignore[arg-type]


class LiteLLMLLM(BasePipelineElement):
    """LLM pipeline element that uses LiteLLM as a universal interface.

    Args:
        model: The LiteLLM model string (e.g., "gpt-4o", "anthropic/claude-3-5-sonnet-20241022").
            Supports suffixes for extended reasoning:
            - ``-thinking-N`` for Anthropic thinking budget (e.g., "anthropic/claude-3-7-sonnet-20250219-thinking-16000")
            - ``-reasoning-LEVEL`` for OpenAI reasoning effort (e.g., "openai/o3-mini-reasoning-medium")
              where LEVEL is one of: low, medium, high, xhigh
        temperature: The temperature to use for generation.
        **litellm_kwargs: Extra keyword arguments passed to litellm.completion()
            (e.g., api_base, api_key).
    """

    def __init__(self, model: str, temperature: float = 0.0, **litellm_kwargs) -> None:
        self.temperature = temperature
        self.litellm_kwargs = litellm_kwargs
        self.thinking_budget: int | None = None
        self.reasoning_effort: str | None = None

        thinking_match = _THINKING_PATTERN.match(model)
        reasoning_match = _REASONING_PATTERN.match(model)
        if thinking_match:
            self.model = thinking_match.group(1)
            self.thinking_budget = int(thinking_match.group(2))
        elif reasoning_match:
            self.model = reasoning_match.group(1)
            self.reasoning_effort = reasoning_match.group(2)
        else:
            self.model = model

    def query(
        self,
        query: str,
        runtime: FunctionsRuntime,
        env: Env = EmptyEnv(),
        messages: Sequence[ChatMessage] = [],
        extra_args: dict = {},
    ) -> tuple[str, FunctionsRuntime, Env, Sequence[ChatMessage], dict]:
        openai_messages = [_message_to_openai(message, self.model) for message in messages]
        openai_tools = [_function_to_openai(tool) for tool in runtime.functions.values()]
        completion = cast(
            ModelResponse,
            _litellm_completion(
                self.model,
                openai_messages,
                openai_tools,
                self.temperature,
                self.thinking_budget,
                self.reasoning_effort,
                self.litellm_kwargs,
            ),
        )
        output = _openai_to_assistant_message(cast(ChatCompletionMessage, completion.choices[0].message))
        messages = [*messages, output]
        return query, runtime, env, messages, extra_args


class LiteLLMToolFilter(BasePipelineElement):
    """Filters tools to only those relevant to the user's task, using LiteLLM.

    Args:
        prompt: The prompt instructing the model to list relevant tools.
        model: The LiteLLM model string.
        temperature: The temperature to use for generation.
    """

    def __init__(self, prompt: str, model: str, temperature: float = 0.0) -> None:
        self.prompt = prompt
        self.model = model
        self.temperature = temperature

    def query(
        self,
        query: str,
        runtime: FunctionsRuntime,
        env: Env = EmptyEnv(),
        messages: Sequence[ChatMessage] = [],
        extra_args: dict = {},
    ) -> tuple[str, FunctionsRuntime, Env, Sequence[ChatMessage], dict]:
        messages = [*messages, ChatUserMessage(role="user", content=[text_content_block_from_string(self.prompt)])]
        openai_messages = [_message_to_openai(message, self.model) for message in messages]
        openai_tools = [_function_to_openai(tool) for tool in runtime.functions.values()]
        completion = cast(
            ModelResponse,
            litellm.completion(
                model=self.model,
                messages=openai_messages,  # type: ignore[arg-type]
                tools=openai_tools or None,
                tool_choice="none",
                temperature=self.temperature,
            ),
        )
        output = _openai_to_assistant_message(cast(ChatCompletionMessage, completion.choices[0].message))

        new_tools = {}
        for tool_name, tool in runtime.functions.items():
            if output["content"] is not None and tool_name in get_text_content_as_str(output["content"]):
                new_tools[tool_name] = tool

        runtime.update_functions(new_tools)

        messages = [*messages, output]
        return query, runtime, env, messages, extra_args
