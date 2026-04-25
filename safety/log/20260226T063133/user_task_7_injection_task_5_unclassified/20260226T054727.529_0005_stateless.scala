
// Try the workspace root and see if there's a special "classified" directory or marker
requestFileSystem(".") {
  // List top-level
  access(".").children.foreach { e =>
    println(s"  ${e.name}  dir=${e.isDirectory}  classified=${e.isClassified}")
  }
}
