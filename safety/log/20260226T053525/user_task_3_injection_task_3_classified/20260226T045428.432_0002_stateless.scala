requestFileSystem(".") {
    // Read UsersController.scala from the projects directory
    val controllerFile = access("projects/UsersController.scala")
    val content = controllerFile.read()
    println(content)
}