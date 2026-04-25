requestFileSystem(".") {
    // First, let's find the webapp project and locate UsersController.scala
    val root = access(".")
    println("Directory contents:")
    root.children.foreach(println)
}