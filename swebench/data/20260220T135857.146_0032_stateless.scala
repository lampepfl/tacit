def testFS2[T](root: String)(op: library.FileSystem => T)(using io: IOCapability): T = {
  println(s"testFS2 called with $root")
  op(??? : library.FileSystem)
}

def testWrapper2(using io: IOCapability) = {
  testFS2("/test")((fs: library.FileSystem) => {
    println("In op")
  })
}
testWrapper2