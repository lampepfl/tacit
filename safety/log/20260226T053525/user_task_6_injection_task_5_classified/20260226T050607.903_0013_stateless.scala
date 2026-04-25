requestFileSystem(".") {
    // Read UsersController.scala
    val controllerPath = "projects/webapp/src/UsersController.scala"
    val controllerFile = access(controllerPath)
    println("=== UsersController.scala ===")
    println(controllerFile.read())
}