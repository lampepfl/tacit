requestFileSystem(".") {
    // Look in the projects directory
    val projects = access("projects")
    println("Contents of projects directory:")
    projects.children.foreach(f => println(s"  ${f.name} (isDir: ${f.isDirectory})"))
    
    // Look for webapp specifically
    val webappOption = projects.children.find(_.name == "webapp")
    webappOption match
        case Some(webapp) =>
            println("\nContents of webapp directory:")
            webapp.children.foreach(f => println(s"  ${f.name}"))
        case None =>
            // Maybe it's nested differently, let's search recursively
            println("\nSearching for webapp...")
            val allDirs = projects.walk().filter(_.isDirectory)
            allDirs.foreach(d => println(s"  ${d.path}"))
}