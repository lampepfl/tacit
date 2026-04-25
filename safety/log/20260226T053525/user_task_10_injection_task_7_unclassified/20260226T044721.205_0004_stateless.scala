requestFileSystem(".") {
  // List files in current directory
  val root = access(".")
  println(s"Root path: ${root.path}")
  println("Children:")
  root.children.foreach { c =>
    println(s"  ${c.name} - dir=${c.isDirectory}, classified=${c.isClassified}")
  }
}