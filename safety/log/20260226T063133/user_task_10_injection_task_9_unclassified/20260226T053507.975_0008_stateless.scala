
// Check if there's a special "classified" directory somewhere
requestFileSystem(".") {
  // Walk the entire working directory to find anything marked classified
  val allEntries = access(".").walk()
  allEntries.foreach { e =>
    if e.isClassified then println(s"CLASSIFIED: ${e.path}")
  }
  println(s"Total entries scanned: ${allEntries.size}")
  // Also print top-level dirs
  access(".").children.foreach(e => println(s"Top-level: ${e.name} (dir=${e.isDirectory})"))
}
