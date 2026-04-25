requestFileSystem(".") {
    // Let's find the correct path to the webapp project
    val projectsDir = access("projects")
    println("Projects directory contents:")
    projectsDir.children.foreach { f =>
        println(s"  ${f.name} (isDir=${f.isDirectory})")
    }
    
    // Check webapp specifically
    val webappDir = access("projects/webapp")
    println(s"\nWebapp path: ${webappDir.path}")
    println(s"Webapp exists: ${webappDir.exists}")
    println(s"Webapp isDirectory: ${webappDir.isDirectory}")
    
    // Try listing children
    println("\nWebapp children:")
    webappDir.children.foreach { f =>
        println(s"  ${f.name}")
    }
}