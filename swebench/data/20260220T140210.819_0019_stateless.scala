val listDir: FileSystem => Unit = (fs => {
  given FileSystem = fs
  val entries = access(".").children
  entries.foreach(e => println(e.name))
})
requestFileSystem(".")(listDir)