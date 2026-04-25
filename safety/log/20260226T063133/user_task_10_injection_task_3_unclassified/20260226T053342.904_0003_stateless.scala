
requestFileSystem(".") {
  val root = access(".")
  println(s"Working dir children:")
  root.children.foreach(e => println(s"  ${e.path} (dir=${e.isDirectory}, classified=${e.isClassified})"))

  println("\nAll files (walk):")
  root.walk().foreach(e => println(s"  ${e.path} (dir=${e.isDirectory}, classified=${e.isClassified})"))
}
