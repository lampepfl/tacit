requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp project
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(f => println(s"  ${f.name}"))
    
    // Let's look for webapp directory
    val webappOpt = root.children.find(_.name == "webapp")
    webappOpt match
        case Some(webapp) =>
            println("\nFound webapp directory, contents:")
            webapp.children.foreach(f => println(s"  ${f.name}"))
        case None =>
            println("\nNo webapp directory found directly, searching recursively...")
            val allFiles = root.walk().filter(_.name.endsWith("UsersController.scala"))
            allFiles.foreach(f => println(s"Found: ${f.path}"))
}