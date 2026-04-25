val listDir: FileSystem => Unit = (fs: FileSystem) => {
  val entries = access(".").children
  entries.foreach(e => println(e.name))
}
requestFileSystem(".")(listDir)