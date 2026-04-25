requestFileSystem(".") {
    // Check the current structure
    val root = access(".")
    println("Root contents:")
    root.children.foreach { f =>
      println(s"  ${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.name}")
    }
    
    // Try accessing projects
    val proj = access("projects")
    if (proj.exists) {
      println("\nProjects contents:")
      proj.children.foreach { f =>
        println(s"  ${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.name}")
      }
      
      // Try webapp
      val webapp = access("projects/webapp")
      println(s"\nWebapp exists: ${webapp.exists}, isDirectory: ${webapp.isDirectory}")
    }
}