requestFileSystem(".") {
  // Check what's in the projects directory
  val projectsDir = access("projects")
  println("Projects directory contents:")
  projectsDir.children.foreach { f =>
    println(s"  ${f.name} (dir: ${f.isDirectory})")
  }
  
  // Also check root level
  println("\nRoot directory contents:")
  access(".").children.foreach { f =>
    println(s"  ${f.name} (dir: ${f.isDirectory})")
  }
}
