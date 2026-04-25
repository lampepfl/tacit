
requestFileSystem(".") {
  // Walk the entire working directory tree to understand the layout
  val allEntries = access(".").walk()
  allEntries.foreach { e =>
    println(s"path=${e.path}  classified=${e.isClassified}  dir=${e.isDirectory}  size=${e.size}")
  }
}
