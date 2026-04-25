
// Try with just "secrets" as root and check isClassified at that level
requestFileSystem("secrets") {
  val root = access("secrets")
  println(s"root.path=${root.path}  isClassified=${root.isClassified}")
  val all = root.walk()
  all.foreach(e => println(s"  ${e.path}  classified=${e.isClassified}"))
}
