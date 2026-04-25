requestFileSystem(".") {
    // Explore the projects directory
    val projects = access("projects")
    println("projects directory contents:")
    projects.children.foreach(f => println(s"  ${f.name}"))
    
    // Find webapp
    val webappOpt = projects.children.find(_.name == "webapp")
    webappOpt match
        case Some(webapp) =>
            println("\nwebapp directory contents:")
            webapp.children.foreach(f => println(s"  ${f.name}"))
        case None =>
            println("webapp directory not found")
}