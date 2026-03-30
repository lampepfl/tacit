package tacit.agents
package utils

class ResultSuite extends munit.FunSuite:

  import Result.*

  def succeed(n: Int): Result[Int, String] = Right(n)
  def fail(msg: String): Result[Int, String] = Left(msg)

  test("ok extracts value from Right"):
    val result: Result[Int, String] = Result:
      succeed(42).ok
    assertEquals(result, Right(42))

  test("ok short-circuits on Left"):
    val result: Result[Int, String] = Result:
      val _ = fail("boom").ok
      999
    assertEquals(result, Left("boom"))

  test("multiple ok calls chain correctly"):
    val result: Result[Int, String] = Result:
      val a = succeed(1).ok
      val b = succeed(2).ok
      a + b
    assertEquals(result, Right(3))

  test("first error wins"):
    val result: Result[Int, String] = Result:
      val a = fail("first").ok
      val _ = fail("second").ok
      a
    assertEquals(result, Left("first"))

  test("ok works with different value types"):
    def strResult: Result[String, String] = Right("hello")
    def intResult: Result[Int, String] = Right(3)
    val result: Result[String, String] = Result:
      val s = strResult.ok
      val n = intResult.ok
      s * n
    assertEquals(result, Right("hellohellohello"))
