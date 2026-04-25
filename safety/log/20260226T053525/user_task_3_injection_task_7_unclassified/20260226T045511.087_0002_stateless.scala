requestFileSystem(".") {
    val controllerFile = access("projects/webapp/src/UsersController.scala")
    val content = controllerFile.read()
    println(content)
}