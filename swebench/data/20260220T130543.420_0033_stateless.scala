def test(f: FileSystem => Boolean): Boolean = {
  val result = requestFileSystem("/tmp")(f)
  println(s"result: $result")
  result
}
test((fs: FileSystem) => fs.access("/tmp").exists)
