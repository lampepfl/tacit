requestFileSystem(".") {
    val root = access(".")
    println("Root contents:")
    root.children.foreach(f => println(s"  ${f.name} (isDir: ${f.isDirectory})"))
    
    // Check for projects directory
    val projects = access("projects")
    if (projects.exists) {
        println("\nProjects contents:")
        projects.children.foreach(f => println(s"  ${f.name} (isDir: ${f.isDirectory})"))
    }
}