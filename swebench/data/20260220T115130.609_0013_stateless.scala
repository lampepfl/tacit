def listDir(path: String)(using FileSystem): String = {
  val name = access(path).name
  name
}
requestFileSystem("/tmp")(listDir)