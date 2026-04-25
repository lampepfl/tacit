def test2Params(arg: String)(op: String => Unit)(using io: IOCapability) = {
  println(s"test2Params called with $arg")
  op(arg)
}

def testWrapper(using io: IOCapability) = {
  test2Params("hello")(s => println(s"op called with $s"))
}
testWrapper