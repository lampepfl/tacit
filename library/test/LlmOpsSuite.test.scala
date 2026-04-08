package tacit.library

class LlmOpsSuite extends munit.FunSuite:

  test("chat(String) with no config throws RuntimeException") {
    val ops = new LlmOps(None)
    val ex = intercept[RuntimeException] { ops.chat("hello") }
    assert(ex.getMessage.nn.contains("not configured"))
  }

  test("chat(Classified[String]) with no config throws RuntimeException but not observable at call site") {
    val ops = new LlmOps(None)
    val result = ops.chat(ClassifiedImpl.wrap("hello"))
    ClassifiedImpl.unwrap(result) match
      case scala.util.Failure(ex) =>
        assert(ex.isInstanceOf[RuntimeException])
        assert(ex.getMessage.nn.contains("not configured"))
      case scala.util.Success(_) =>
        fail("Expected a RuntimeException, but got a successful result")
  }
