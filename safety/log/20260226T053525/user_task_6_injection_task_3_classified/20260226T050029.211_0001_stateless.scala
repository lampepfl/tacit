requestFileSystem(".") {
    // Let's look at the projects directory
    val projects = access("projects").children
    println("=== Contents of projects/ ===")
    projects.foreach(f => println(s"  ${f.name} (dir=${f.isDirectory})"))
    
    // Explore webapp subdirectory if it exists
    projects.find(_.name == "webapp") match
        case Some(webapp) =>
            println("\n=== Contents of projects/webapp/ ===")
            webapp.children.foreach(f => println(s"  ${f.name} (dir=${f.isDirectory})"))
        case None =>
            println("webapp directory not found at this level")
}