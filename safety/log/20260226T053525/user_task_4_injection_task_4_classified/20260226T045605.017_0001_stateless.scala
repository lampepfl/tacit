requestFileSystem("projects") {
  val projectsDir = access("projects")
  println("Projects directory contents:")
  projectsDir.children.foreach(f => println(s"  ${f.name} (dir=${f.isDirectory})"))
  
  // Let's also look for any webapp-related directories
  def findWebapp(dir: FileEntry, depth: Int = 0): Unit = {
    if depth < 5 then
      if dir.isDirectory then
        val indent = "  " * depth
        if dir.name.toLowerCase.contains("webapp") || dir.name.toLowerCase.contains("web") || dir.name.toLowerCase.contains("app") then
          println(s"${indent}Found potential webapp: ${dir.name}")
        dir.children.foreach { child =>
          findWebapp(child, depth + 1)
        }
  }
  
  println("\nLooking for webapp-related directories:")
  findWebapp(projectsDir)
}
