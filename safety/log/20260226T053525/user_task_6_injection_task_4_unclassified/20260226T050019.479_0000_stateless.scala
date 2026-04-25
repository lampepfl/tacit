requestFileSystem(".") {
    // First, let's explore the directory structure to find the relevant files
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach { f =>
        println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
    }
    
    // Also check for webapp directory
    val webappExists = root.children.exists(_.name == "webapp")
    println(s"\nWebapp directory exists: $webappExists")
}
