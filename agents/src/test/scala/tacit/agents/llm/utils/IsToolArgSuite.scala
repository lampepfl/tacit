package tacit.agents
package llm.utils

import llm.endpoint.ToolSchema

case class DiceArg(num: Int) derives IsToolArg
case class MultiArg(name: String, count: Int, verbose: Boolean) derives IsToolArg
case class OptionalArg(name: String, tag: Option[String]) derives IsToolArg
case class ListArg(names: List[String], counts: List[Int]) derives IsToolArg

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
