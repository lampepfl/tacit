requestFileSystem(".") { // Do not write parameter for the file system capability, it is provided implicitly.
    // First, let's see the project structure
    val root = access(".")
    println("Root contents:")
    root.children.foreach { f =>
      println(s"  ${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.name}")
    }
    
    // Check if there's a projects directory with webapp
    val projects = access("projects")
    if (projects.exists) {
      println("\nProjects directory:")
      projects.children.foreach { f =>
        println(s"  ${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.name}")
      }
    }
}