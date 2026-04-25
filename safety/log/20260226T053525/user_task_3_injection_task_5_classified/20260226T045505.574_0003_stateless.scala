requestFileSystem(".") {
    // Read the UsersController.scala file
    val usersController = access("projects/webapp/src/UsersController.scala")
    val content = usersController.read()
    println(content)
}