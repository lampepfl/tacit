requestFileSystem(".") {
    // Look at the projects directory more closely
    val projects = access("projects")
    println("=== Projects directory ===")
    projects.children.foreach(f => println(s"${f.name} (isDir: ${f.isDirectory})"))
    
    // Let's check if webapp is under projects
    val webapp = access("projects/webapp")
    if (webapp.exists) {
        println("\n=== Webapp directory contents ===")
        webapp.children.foreach(f => println(s"${f.name} (isDir: ${f.isDirectory})"))
    }
}