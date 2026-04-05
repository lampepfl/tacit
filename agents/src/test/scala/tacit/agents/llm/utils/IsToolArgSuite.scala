package tacit.agents
package llm.utils

import llm.endpoint.ToolSchema

/** @param num Number of dice to roll */
case class DiceArg(num: Int) derives IsToolArg
case class MultiArg(
  @desc("The player name") name: String,
  @desc("How many items") count: Int,
  @desc("Enable verbose output") verbose: Boolean,
) derives IsToolArg
case class OptionalArg(name: String, tag: Option[String]) derives IsToolArg
case class ListArg(names: List[String], counts: List[Int]) derives IsToolArg

// --- Additional test case classes for @param parsing ---

/** @param query The search query
  * @param maxResults Maximum number of results
  */
case class MultiParamDoc(query: String, maxResults: Int) derives IsToolArg

/** A tool for greeting users.
  *
  * This tool says hello.
  *
  * @param name The name to greet
  */
case class DocWithProse(name: String) derives IsToolArg

/** @param x The x coordinate
  * @param y The y coordinate
  * @return the distance from origin
  */
case class MixedTags(x: Int, y: Int) derives IsToolArg

/** @param city The city name
  * @param country The country
  *   where the city is located
  */
case class MultiLineParam(city: String, country: String) derives IsToolArg

/**
  * @param name   The    user's   name
  */
case class ExtraWhitespace(name: String) derives IsToolArg

/** @param described Has a description
  * @param undescribed
  */
case class PartiallyDescribed(described: String, undescribed: String) derives IsToolArg

/** @param a First
  * @param b Second
  * @param c Third
  * @param d Fourth
  */
case class ManyParams(a: Int, b: Int, c: Int, d: Int) derives IsToolArg

/** @param first Has @special chars & symbols! */
case class SpecialCharsInDesc(first: String) derives IsToolArg

/** Some class description. */
case class DocButNoParams(value: Int) derives IsToolArg

/** @param annotated From annotation
  */
case class DescOverridesParam(@desc("From @desc") annotated: String) derives IsToolArg

class ToolArgSuite extends munit.FunSuite:

  // --- Schema tests ---

  test("schema: single Int field"):
    val s = summon[IsToolArg[DiceArg]].schema
    assertEquals(s.properties.size, 1)
    assertEquals(s.properties("num").`type`, "integer")
    assertEquals(s.required, List("num"))

  test("schema: multi-field"):
    val s = summon[IsToolArg[MultiArg]].schema
    assertEquals(s.properties.size, 3)
    assertEquals(s.properties("name").`type`, "string")
    assertEquals(s.properties("count").`type`, "integer")
    assertEquals(s.properties("verbose").`type`, "boolean")
    assertEquals(s.required.sorted, List("count", "name", "verbose"))

  test("schema: optional fields not in required"):
    val s = summon[IsToolArg[OptionalArg]].schema
    assertEquals(s.properties.size, 2)
    assertEquals(s.properties("tag").`type`, "string")
    assertEquals(s.required, List("name"))

  test("schema: list fields have array type with items"):
    val s = summon[IsToolArg[ListArg]].schema
    assertEquals(s.properties("names").`type`, "array")
    assert(s.properties("names").items.isDefined)
    assertEquals(s.properties("names").items.get.`type`, "string")
    assertEquals(s.properties("counts").`type`, "array")
    assertEquals(s.properties("counts").items.get.`type`, "integer")

  // --- Docstring description tests ---

  test("schema: field descriptions from @param tags"):
    val s = summon[IsToolArg[DiceArg]].schema
    assertEquals(s.properties("num").description, "Number of dice to roll")

  test("schema: field descriptions from @desc annotations"):
    val s = summon[IsToolArg[MultiArg]].schema
    assertEquals(s.properties("name").description, "The player name")
    assertEquals(s.properties("count").description, "How many items")
    assertEquals(s.properties("verbose").description, "Enable verbose output")

  test("schema: fields without docstrings have empty description"):
    val s = summon[IsToolArg[OptionalArg]].schema
    assertEquals(s.properties("name").description, "")
    assertEquals(s.properties("tag").description, "")

  test("schema: multiple @param tags"):
    val s = summon[IsToolArg[MultiParamDoc]].schema
    assertEquals(s.properties("query").description, "The search query")
    assertEquals(s.properties("maxResults").description, "Maximum number of results")

  test("schema: @param with prose before it"):
    val s = summon[IsToolArg[DocWithProse]].schema
    assertEquals(s.properties("name").description, "The name to greet")

  test("schema: @param mixed with other tags like @return"):
    val s = summon[IsToolArg[MixedTags]].schema
    assertEquals(s.properties("x").description, "The x coordinate")
    assertEquals(s.properties("y").description, "The y coordinate")

  test("schema: multi-line @param description"):
    val s = summon[IsToolArg[MultiLineParam]].schema
    assertEquals(s.properties("city").description, "The city name")
    assertEquals(s.properties("country").description, "The country where the city is located")

  test("schema: extra whitespace in @param"):
    val s = summon[IsToolArg[ExtraWhitespace]].schema
    assertEquals(s.properties("name").description, "The    user's   name")

  test("schema: @param with no description text"):
    val s = summon[IsToolArg[PartiallyDescribed]].schema
    assertEquals(s.properties("described").description, "Has a description")
    assertEquals(s.properties("undescribed").description, "")

  test("schema: many @param tags"):
    val s = summon[IsToolArg[ManyParams]].schema
    assertEquals(s.properties("a").description, "First")
    assertEquals(s.properties("b").description, "Second")
    assertEquals(s.properties("c").description, "Third")
    assertEquals(s.properties("d").description, "Fourth")

  test("schema: special characters in description"):
    val s = summon[IsToolArg[SpecialCharsInDesc]].schema
    assertEquals(s.properties("first").description, "Has @special chars & symbols!")

  test("schema: class docstring without @param tags"):
    val s = summon[IsToolArg[DocButNoParams]].schema
    assertEquals(s.properties("value").description, "")

  test("schema: @desc annotation overrides @param tag"):
    val s = summon[IsToolArg[DescOverridesParam]].schema
    assertEquals(s.properties("annotated").description, "From @desc")

  // --- parseParamTags unit tests ---

  test("parseParamTags: None input"):
    assertEquals(IsToolArg.parseParamTags(None), Map.empty)

  test("parseParamTags: single @param"):
    val raw = Some("/** @param name The user name */")
    assertEquals(IsToolArg.parseParamTags(raw), Map("name" -> "The user name"))

  test("parseParamTags: multiple @param tags"):
    val raw = Some(
      """/** @param x First
        |  * @param y Second
        |  */""".stripMargin)
    assertEquals(IsToolArg.parseParamTags(raw), Map("x" -> "First", "y" -> "Second"))

  test("parseParamTags: @param mixed with @return"):
    val raw = Some(
      """/** @param a Alpha
        |  * @return something
        |  * @param b Beta
        |  */""".stripMargin)
    val result = IsToolArg.parseParamTags(raw)
    assertEquals(result("a"), "Alpha")
    assertEquals(result("b"), "Beta")
    assert(!result.contains("return"))

  test("parseParamTags: @param with no description"):
    val raw = Some("/** @param lonely */")
    val result = IsToolArg.parseParamTags(raw)
    assertEquals(result.getOrElse("lonely", ""), "")

  test("parseParamTags: docstring with no @param"):
    val raw = Some("/** Just a description. */")
    assertEquals(IsToolArg.parseParamTags(raw), Map.empty)

  test("parseParamTags: @param with extra whitespace"):
    val raw = Some("/**  @param   name    A description  */")
    assertEquals(IsToolArg.parseParamTags(raw), Map("name" -> "A description"))

  test("parseParamTags: empty docstring"):
    val raw = Some("/** */")
    assertEquals(IsToolArg.parseParamTags(raw), Map.empty)

  test("parseParamTags: @param followed by @throws"):
    val raw = Some(
      """/** @param input The input value
        |  * @throws RuntimeException if input is negative
        |  */""".stripMargin)
    val result = IsToolArg.parseParamTags(raw)
    assertEquals(result, Map("input" -> "The input value"))

  test("parseParamTags: prose before @param"):
    val raw = Some(
      """/** This is a utility method.
        |  *
        |  * It does useful things.
        |  *
        |  * @param foo The foo value
        |  */""".stripMargin)
    assertEquals(IsToolArg.parseParamTags(raw), Map("foo" -> "The foo value"))

  test("parseParamTags: many @param tags"):
    val raw = Some(
      """/** @param a First
        |  * @param b Second
        |  * @param c Third
        |  * @param d Fourth
        |  * @param e Fifth
        |  */""".stripMargin)
    val result = IsToolArg.parseParamTags(raw)
    assertEquals(result.size, 5)
    assertEquals(result("a"), "First")
    assertEquals(result("e"), "Fifth")

  // --- Parse tests ---

  test("parse: simple case class"):
    val result = summon[IsToolArg[DiceArg]].parse("""{"num": 3}""")
    assertEquals(result, Right(DiceArg(3)))

  test("parse: multi-field"):
    val result = summon[IsToolArg[MultiArg]].parse(
      """{"name": "test", "count": 5, "verbose": true}"""
    )
    assertEquals(result, Right(MultiArg("test", 5, true)))

  test("parse: optional field present"):
    val result = summon[IsToolArg[OptionalArg]].parse(
      """{"name": "hello", "tag": "world"}"""
    )
    assertEquals(result, Right(OptionalArg("hello", Some("world"))))

  test("parse: optional field absent"):
    val result = summon[IsToolArg[OptionalArg]].parse(
      """{"name": "hello"}"""
    )
    assertEquals(result, Right(OptionalArg("hello", None)))

  test("parse: optional field explicit null"):
    val result = summon[IsToolArg[OptionalArg]].parse(
      """{"name": "hello", "tag": null}"""
    )
    assertEquals(result, Right(OptionalArg("hello", None)))

  test("parse: list fields"):
    val result = summon[IsToolArg[ListArg]].parse(
      """{"names": ["a", "b"], "counts": [1, 2, 3]}"""
    )
    assertEquals(result, Right(ListArg(List("a", "b"), List(1, 2, 3))))

  test("parse: invalid JSON returns Left with invalidJson error"):
    val result = summon[IsToolArg[DiceArg]].parse("not json")
    assert(result.isLeft)
    assert(result.swap.getOrElse(fail("expected Left")).message.startsWith("Invalid JSON"))

  test("parse: wrong type returns Left with typeMismatch error"):
    val result = summon[IsToolArg[DiceArg]].parse("""{"num": "hello"}""")
    assert(result.isLeft)
    assert(result.swap.getOrElse(fail("expected Left")).message.contains("num"))

  test("parse: missing required field returns Left with missingField error"):
    val result = summon[IsToolArg[MultiArg]].parse("""{"name": "test"}""")
    assert(result.isLeft)
    assert(result.swap.getOrElse(fail("expected Left")).message.contains("Missing required field"))
