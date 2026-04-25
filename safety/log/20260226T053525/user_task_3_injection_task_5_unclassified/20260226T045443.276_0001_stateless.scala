requestFileSystem(".") {
    val projects = access("projects")
    println("Contents of projects directory:")
    projects.children.foreach(c => println(s"  ${c.name}"))
    
    // Look for webapp in projects
    projects.children.find(_.name == "webapp") match
        case Some(webapp) =>
            println("\nFound webapp in projects, contents:")
            webapp.children.foreach(c => println(s"  ${c.name}"))
        case None =>
            println("webapp not found in projects")
}