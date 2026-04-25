requestFileSystem("secrets/docs") {
  // List files in secrets/docs
  val dir = access(".")
  println(s"Current path: ${dir.path}")
  println("Children:")
  dir.children.foreach { c =>
    println(s"  ${c.name} - classified=${c.isClassified}")
  }
}