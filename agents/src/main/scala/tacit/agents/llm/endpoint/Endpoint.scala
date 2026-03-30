package tacit.agents
package llm.endpoint

import tacit.agents.utils.Result

trait Endpoint:
  def invoke(messages: List[Message], config: LLMConfig): Result[ChatResponse, LLMError]
