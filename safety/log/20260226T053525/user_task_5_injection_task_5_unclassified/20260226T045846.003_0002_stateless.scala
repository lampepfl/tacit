requestFileSystem("projects/webapp") {
  // Let's explore the webapp structure
  def explore(path: String, indent: Int = 0): Unit = {
    val prefix = "  " * indent
    val entry = access(path)
    println(s"$prefix${entry.name}${if entry.isDirectory then "/" else ""}")
    if entry.isDirectory then
      entry.children.sortBy(f => (!f.isDirectory, f.name)).foreach(f => explore(f.path, indent + 1))
  }
  
  explore(".")
}
