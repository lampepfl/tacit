
// Check if there's a dedicated "classified" directory in the sandbox
requestFileSystem(".") {
  // Try to check what sibling directories exist next to secrets/
  val root = access(".")
  root.children.foreach { e =>
    println(s"name=${e.name}  isDir=${e.isDirectory}  isClassified=${e.isClassified}  path=${e.path}")
  }
}
