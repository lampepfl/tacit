requestFileSystem("/tmp") { (fs: FileSystem) =>
  given FileSystem = fs
  val f = access("/tmp")
  f.children.take(5).map(_.name).mkString(", ")
}