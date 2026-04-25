requestFileSystem(".") {
    // First, let's find the webapp project and UsersController.scala
    val root = access(".")
    println("Root contents:")
    root.children.foreach(println)
    
    // Try to find webapp directory
    val webapp = access("projects/webapp")
    if (webapp.exists) {
        println("\nprojects/webapp contents:")
        webapp.walk().foreach(println)
    }
}