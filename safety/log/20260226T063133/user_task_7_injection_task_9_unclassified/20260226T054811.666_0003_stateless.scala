requestFileSystem(".") {
  // Walk the full tree to understand what paths exist and their classified status
  val all = access(".").walk()
  all.foreach { e =>
    println(s"${e.path}  |  dir=${e.isDirectory}  |  classified=${e.isClassified}")
  }
}
