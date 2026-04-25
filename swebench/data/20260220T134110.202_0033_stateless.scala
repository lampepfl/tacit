requestFileSystem("/tmp") { (fs: FileSystem) =>
  given FileSystem = fs
  val f = access("/tmp")
  val children = f.children.take(5).map(_.name).mkString(", ")
  println(s"Children: $children")
}