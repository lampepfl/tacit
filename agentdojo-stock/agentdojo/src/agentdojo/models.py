DEFAULT_MODEL_DISPLAY_NAME = "the AI language model"

_MODEL_NAMES_OVERRIDES: dict[str, str] = {
    "gpt-4o-2024-05-13": "GPT-4",
    "gpt-4o-mini-2024-07-18": "GPT-4",
    "gpt-3.5-turbo-0125": "GPT-3.5",
    "gpt-4-turbo-2024-04-09": "GPT-4",
    "gpt-4-0125-preview": "GPT-4",
    "claude-3-opus-20240229": "Claude",
    "claude-3-sonnet-20240229": "Claude",
    "claude-3-5-sonnet-20240620": "Claude",
    "claude-3-5-sonnet-20241022": "Claude",
    "claude-3-7-sonnet-20250219": "Claude",
    "claude-3-haiku-20240307": "Claude",
    "command-r-plus": "Command R+",
    "command-r": "Command R",
    "mistralai/Mixtral-8x7B-Instruct-v0.1": "Mixtral",
    "meta-llama/Llama-3-70b-chat-hf": "AI assistant",
    "gemini-1.5-pro-002": "AI model developed by Google",
    "gemini-1.5-pro-001": "AI model developed by Google",
    "gemini-1.5-flash-002": "AI model developed by Google",
    "gemini-1.5-flash-001": "AI model developed by Google",
    "gemini-2.0-flash-exp": "AI model developed by Google",
    "gemini-2.0-flash-001": "AI model developed by Google",
    "gemini-2.5-flash-preview-04-17": "AI model developed by Google",
    "gemini-2.5-pro-preview-05-06": "AI model developed by Google",
}

_HEURISTIC_RULES: list[tuple[str, str]] = [
    ("gpt", "GPT-4"),
    ("claude", "Claude"),
    ("command-r", "Command R+"),
    ("gemini", "AI model developed by Google"),
    ("mixtral", "Mixtral"),
    ("mistral", "Mixtral"),
    ("llama", "AI assistant"),
]


def infer_model_display_name(model_id: str) -> str:
    """Infer a human-readable display name for a model.

    First checks an explicit overrides dict for known models, then
    falls back to substring-based heuristics.

    Args:
        model_id: The model identifier string (may include a provider prefix like "anthropic/...").

    Returns:
        A human-readable display name suitable for use in attack prompts.
    """
    if model_id in _MODEL_NAMES_OVERRIDES:
        return _MODEL_NAMES_OVERRIDES[model_id]

    model_lower = model_id.lower()
    for substring, display_name in _HEURISTIC_RULES:
        if substring in model_lower:
            return display_name

    return DEFAULT_MODEL_DISPLAY_NAME
