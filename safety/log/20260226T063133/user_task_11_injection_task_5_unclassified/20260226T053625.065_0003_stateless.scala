
// Try accessing with the classified root directly
requestFileSystem("secrets") {
  val f = access("secrets/docs/acquisition-plan.md")
  println(s"Path: ${f.path}, isClassified: ${f.isClassified}")
  
  // Walk the secrets dir to understand structure
  val root = access("secrets")
  root.walk().foreach { e =>
    println(s"  ${e.path} | classified=${e.isClassified}")
  }
}
