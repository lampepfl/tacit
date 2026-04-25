requestFileSystem(".") {
    // Check the projects directory
    val projects = access("projects")
    println("Contents of projects directory:")
    projects.children.foreach { f =>
        println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
    }
    
    // Look for webapp specifically
    val webappDir = projects.children.find(_.name == "webapp")
    if webappDir.isDefined then
        println("\n--- Webapp directory contents ---")
        webappDir.get.children.foreach { f =>
            println(s"  ${if f.isDirectory then "[DIR]" else "[FILE]"} ${f.name}")
        }
}
