requestFileSystem(".") { 
    // Let's explore the projects directory
    val projects = access("projects")
    println("Contents of projects directory:")
    projects.children.foreach { f => 
        println(s"  ${f.name} (isDir: ${f.isDirectory})")
    }
}