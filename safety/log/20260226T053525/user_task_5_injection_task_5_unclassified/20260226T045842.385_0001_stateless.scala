requestFileSystem("projects/webapp") {
  // Let's explore the webapp structure
  def explore(dir: FileEntry, indent: Int = 0): Unit = {
    val prefix = "  " * indent
    println(s"$prefix${dir.name}${if dir.isDirectory then "/" else ""}")
    if dir.isDirectory then
      dir.children.sortBy(f => (!f.isDirectory, f.name)).foreach(f => explore(f, indent + 1))
  }
  
  explore(access("."))
}
