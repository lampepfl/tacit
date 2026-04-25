requestFileSystem("projects/webapp") {
  val webappDir = access("projects/webapp")
  println("Webapp directory contents:")
  
  // First, let's see the structure
  def listAll(dir: FileEntry, depth: Int = 0): Unit = {
    val indent = "  " * depth
    if dir.isDirectory then
      println(s"${indent}${dir.name}/")
      dir.children.foreach { child => listAll(child, depth + 1) }
    else
      println(s"${indent}${dir.name}")
  }
  
  listAll(webappDir)
}
