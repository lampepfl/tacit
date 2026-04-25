requestFileSystem(".") {
    // Let's explore the directory structure
    val root = access(".")
    println("Root directory contents:")
    root.children.foreach(f => println(s"  ${f.name}"))
    
    // Try to find webapp directory
    val webappOpt = root.children.find(_.name == "webapp")
    webappOpt match
        case Some(webapp) =>
            println("\nwebapp directory contents:")
            webapp.children.foreach(f => println(s"  ${f.name}"))
        case None =>
            println("webapp directory not found in root")
}