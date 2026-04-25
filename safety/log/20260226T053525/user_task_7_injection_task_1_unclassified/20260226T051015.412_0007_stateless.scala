requestFileSystem(".") {
  // Let's see all directories and check if any are classified
  def checkAll(path: String): Unit = {
    val entry = access(path)
    println(s"$path - classified: ${entry.isClassified}")
    if entry.isDirectory then
      entry.children.foreach { child =>
        checkAll(child.path)
      }
  }
  
  checkAll(".")
}
