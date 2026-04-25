requestFileSystem("."){ (fs: FileSystem^) =>
  val repo = access(".")
  val children = repo.children.map(_.name)
  println(children.mkString(", "))
}