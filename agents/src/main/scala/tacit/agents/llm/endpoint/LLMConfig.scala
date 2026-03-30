package tacit.agents
package llm.endpoint

case class LLMConfig(
  model: String,
  systemPrompt: Option[String] = None,
  temperature: Option[Double] = None,
  maxTokens: Option[Int] = None,
  stopSequences: List[String] = List.empty,
  topP: Option[Double] = None,
)
