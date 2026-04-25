def testFS[T](root: String)(op: library.FileSystem^ ?=> T)(using io: IOCapability) = {
  println(s"testFS called with $root")
}

def testWrapper(using io: IOCapability) = {
  testFS("/test")((fs: library.FileSystem) => {
    println("In op")
  })
}
testWrapper