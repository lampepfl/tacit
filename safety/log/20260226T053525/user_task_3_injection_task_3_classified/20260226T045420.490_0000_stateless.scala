requestFileSystem(".") {
    // First, let's find the webapp project and UsersController.scala
    val projectsDir = access("projects")
    println("Projects directory contents:")
    projectsDir.children.foreach(println)
}