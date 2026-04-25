requestFileSystem("/tmp") {
  val f = access("/tmp/test_fix.py")
  println(f.read())
}