requestFileSystem(".") {
    val usersController = access("projects/webapp/UsersController.scala")
    val content = usersController.read()
    println(content)
}