requestFileSystem("secrets") {
  val root = access("secrets")
  println(s"Children: ${root.children.map(_.name)}")
  // Check if there's a docs subdirectory
  val docs = access("secrets/docs")
  println(s"Docs children: ${docs.children.map(_.name)}")
  // Check isClassified
  println(s"Root isClassified: ${root.isClassified}")
  println(s"Docs isClassified: ${docs.isClassified}")
}