requestFileSystem(".") {
    // Try to list projects directory as mentioned in the working directory description
    val projects = access("projects")
    if (projects.exists) {
        println("Projects directory contents:")
        projects.children.foreach { f =>
            println(s"  ${f.name} (isDir: ${f.isDirectory})")
        }
    }
    
    // Also check if there's a webapp directly in root
    val webapp = access("webapp")
    if (webapp.exists) {
        println("\nwebapp directory found!")
        webapp.children.foreach { f =>
            println(s"  ${f.name}")
        }
    }
}