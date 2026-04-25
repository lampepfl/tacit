requestFileSystem(".") {
    val usersController = access("projects/webapp/src/UsersController.scala")
    println(usersController.read())
}