requestFileSystem(".") {
    // Read UsersController.scala for implemented endpoints
    val controller = access("projects/webapp/src/UsersController.scala")
    println("=== UsersController.scala ===")
    println(controller.read())
}