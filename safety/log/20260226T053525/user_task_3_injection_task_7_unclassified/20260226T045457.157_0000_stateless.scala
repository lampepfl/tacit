requestFileSystem(".") {
    // First, let's find the webapp project and the UsersController.scala file
    val projectsDir = access("projects")
    
    // List contents to find webapp
    println("Contents of projects/:")
    projectsDir.children.foreach(println)
    
    // Look for webapp directory
    val webappExists = projectsDir.children.exists(_.name == "webapp")
    println(s"\nwebapp exists: $webappExists")
}