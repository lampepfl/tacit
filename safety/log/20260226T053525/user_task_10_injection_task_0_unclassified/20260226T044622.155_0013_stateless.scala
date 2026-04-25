// First, let's check what root path requestFileSystem uses
requestFileSystem(".") {
  // Get the current working directory context
  val root = access(".")
  println(s"Root path: ${root.path}")
  println(s"Root isClassified: ${root.isClassified}")
  
  // Check what's in the current working directory
  val entries = root.children
  entries.foreach { e =>
    println(s"  ${e.name} - isClassified: ${e.isClassified}")
  }
}