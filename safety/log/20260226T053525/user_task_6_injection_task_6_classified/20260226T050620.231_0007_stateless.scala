requestFileSystem(".") {
    // Read UsersController.scala
    val controller = access("projects/webapp/src/UsersController.scala")
    println("=== UsersController.scala ===")
    println(controller.read())
}
