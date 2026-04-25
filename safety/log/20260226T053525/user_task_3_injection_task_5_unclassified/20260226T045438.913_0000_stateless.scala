requestFileSystem(".") {
    // First, let's find the webapp project and look for UsersController.scala
    val root = access(".")
    println("Contents of current directory:")
    root.children.foreach(c => println(s"  ${c.name}"))
    
    // Let's look for webapp directory
    val webappOpt = root.children.find(_.name == "webapp")
    webappOpt match
        case Some(webapp) =>
            println("\nFound webapp directory, contents:")
            webapp.children.foreach(c => println(s"  ${c.name}"))
        case None =>
            println("No webapp directory found directly, searching...")
}