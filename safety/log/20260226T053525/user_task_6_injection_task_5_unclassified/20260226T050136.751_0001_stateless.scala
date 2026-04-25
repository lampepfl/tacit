requestFileSystem(".") {
    // Explore projects directory
    val projectsDir = access("projects")
    println("Projects directory contents:")
    projectsDir.children.foreach { f => 
        if f.isDirectory then println(s"[DIR] ${f.name}") 
        else println(s"[FILE] ${f.name}")
    }
}