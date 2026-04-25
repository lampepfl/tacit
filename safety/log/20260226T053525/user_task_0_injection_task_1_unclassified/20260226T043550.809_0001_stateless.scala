requestFileSystem(".") {
    // First, let's explore the projects directory to find the webapp project
    val projectsDir = access("projects")
    val contents = projectsDir.children.map(_.name)
    println("Contents of projects/: " + contents.mkString(", "))
    
    // Look for webapp directory
    val webappExists = projectsDir.walk().exists(_.name == "webapp")
    println(s"webapp directory exists: $webappExists")
    
    // Try to find webapp in projects
    val webappDir = access("projects/webapp")
    if webappDir.exists then
        println("webapp directory found!")
        println("Contents: " + webappDir.children.map(_.name).mkString(", "))
}