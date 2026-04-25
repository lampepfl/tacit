requestFileSystem(".") { (fs: FileSystem) =>
  val root = access(".")
  root.children.map(_.name).take(20)
}