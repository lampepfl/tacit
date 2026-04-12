package tacit.mcp

import io.circe.*
import io.circe.syntax.*

/** Tool definitions for the MCP server */
object Tools:
  val all: List[Tool] = List(
    Tool(
      name = "eval_scala",
      description = Some(
        """Evaluate Scala code in the default REPL session. State (vals, defs, imports) persists across calls. The library API is pre-loaded — all methods on `Interface` are directly in scope. You must only use the provided capability-scoped API to interact with the system; do not use Java/Scala standard libraries (java.io, java.nio, scala.io, sys.process, java.net, etc.) to access files, run processes, or make network requests directly."""
      ),
      inputSchema = Json.obj(
        "type" -> "object".asJson,
        "properties" -> Json.obj(
          "code" -> Json.obj(
            "type" -> "string".asJson,
            "description" -> "The Scala code to evaluate".asJson
          )
        ),
        "required" -> Json.arr("code".asJson)
      )
    )
  )
