package tacit.agents
package llm
package agentic

import llm.utils.{IsToolArg, ToolArgParsingError}
import endpoint.{LLMConfig, ToolSchema}

// --- Test tool definitions ---

case class DiceArgs(count: Int) derives IsToolArg

object RollDice extends AgentTool[AgentState]:
  type ArgType = DiceArgs
  override def name = "rollDice"
  override def description = "Roll some dice"
  override def handle(arg: DiceArgs, state: AgentState): String = s"Rolled ${arg.count} dice"

case class SearchArgs(query: String, maxResults: Option[Int]) derives IsToolArg

object WebSearch extends AgentTool[AgentState]:
  type ArgType = SearchArgs
  override def name = "webSearch"
  override def description = "Search the web"
  override def handle(arg: SearchArgs, state: AgentState): String = s"Searching for ${arg.query}"

case class TagArgs(names: List[String], verbose: Boolean) derives IsToolArg

object TagItems extends AgentTool[AgentState]:
  type ArgType = TagArgs
  override def name = "tagItems"
  override def description = "Tag items with names"
  override def handle(arg: TagArgs, state: AgentState): String = s"Tagged ${arg.names.size} items"

class AgentToolSuite extends munit.FunSuite:

  // --- toolSchema tests ---

  test("toolSchema: name and description"):
    val schema = RollDice.toolSchema
    assertEquals(schema.name, "rollDice")
    assertEquals(schema.description, "Roll some dice")

  test("toolSchema: parameters match Args fields"):
    val schema = RollDice.toolSchema
    assertEquals(schema.parameters.properties.size, 1)
    assertEquals(schema.parameters.properties("count").`type`, "integer")
    assertEquals(schema.parameters.required, List("count"))

  test("toolSchema: optional fields not required"):
    val schema = WebSearch.toolSchema
    assertEquals(schema.parameters.properties.size, 2)
    assertEquals(schema.parameters.properties("query").`type`, "string")
    assertEquals(schema.parameters.properties("maxResults").`type`, "integer")
    assertEquals(schema.parameters.required, List("query"))

  test("toolSchema: list fields have array type with items"):
    val schema = TagItems.toolSchema
    assertEquals(schema.parameters.properties("names").`type`, "array")
    assert(schema.parameters.properties("names").items.isDefined)
    assertEquals(schema.parameters.properties("names").items.get.`type`, "string")

  // --- parseArgs tests ---

  test("parseArgs: simple tool"):
    val result = RollDice.parseArgs("""{"count": 4}""")
    assertEquals(result, Right(DiceArgs(4)))

  test("parseArgs: tool with optional field present"):
    val result = WebSearch.parseArgs("""{"query": "scala 3", "maxResults": 10}""")
    assertEquals(result, Right(SearchArgs("scala 3", Some(10))))

  test("parseArgs: tool with optional field absent"):
    val result = WebSearch.parseArgs("""{"query": "scala 3"}""")
    assertEquals(result, Right(SearchArgs("scala 3", None)))

  test("parseArgs: tool with list field"):
    val result = TagItems.parseArgs("""{"names": ["a", "b"], "verbose": true}""")
    assertEquals(result, Right(TagArgs(List("a", "b"), true)))

  test("parseArgs: invalid JSON"):
    val result = RollDice.parseArgs("not json")
    assert(result.isLeft)
    assert(result.swap.getOrElse(fail("expected Left")).message.startsWith("Invalid JSON"))

  test("parseArgs: wrong type"):
    val result = RollDice.parseArgs("""{"count": "five"}""")
    assert(result.isLeft)
    assert(result.swap.getOrElse(fail("expected Left")).message.contains("count"))

  test("parseArgs: missing required field"):
    val result = WebSearch.parseArgs("""{"maxResults": 5}""")
    assert(result.isLeft)
    assert(result.swap.getOrElse(fail("expected Left")).message.contains("Missing required field"))

  test("parseArgs: empty object for tool with all required fields"):
    val result = RollDice.parseArgs("""{}""")
    assert(result.isLeft)
