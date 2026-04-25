def runFileSystem[T](op: FileSystem => T): T = {
  val fs = requestFileSystem("/tmp") { (fs: FileSystem) => fs.asInstanceOf[Object] }
  ???
}
