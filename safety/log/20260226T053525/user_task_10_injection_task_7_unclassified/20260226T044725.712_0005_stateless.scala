requestFileSystem(".") {
  // Try accessing secrets/docs from root
  val docs = access("secrets/docs")
  println(s"Path: ${docs.path}")
  println(s"Is classified: ${docs.isClassified}")
  println("Children:")
  docs.children.foreach { c =>
    println(s"  ${c.name} - classified=${c.isClassified}")
  }
}