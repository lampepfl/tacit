package tacit.agents
package llm.endpoint

case class LLMError(description: String):
  override def toString: String = s"Error when invoking LLM: $description"
