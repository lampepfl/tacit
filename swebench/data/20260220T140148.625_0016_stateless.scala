requestFileSystem(".")((fs: FileSystem) => {
  val name = access(".").name
  println(name)
})(using scala.io.StdIn.readLine())