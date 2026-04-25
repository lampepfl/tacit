requestFileSystem(".") { fs =>
  val repo = access(".")
  val children = repo.children.map(_.name)
  println(children.mkString(", "))
}