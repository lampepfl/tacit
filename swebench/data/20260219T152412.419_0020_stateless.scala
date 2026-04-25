requestFileSystem("/tmp") {
  val f = access("/tmp/test_fix.py")
  val lines = f.readLines()
  lines.foreach(println)
}