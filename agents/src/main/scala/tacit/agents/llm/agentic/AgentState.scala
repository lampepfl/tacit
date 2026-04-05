package tacit.agents
package llm
package agentic

import endpoint.{LLMConfig, Message}

trait AgentState:
  val llmConfig: LLMConfig
  val messages: List[Message]
