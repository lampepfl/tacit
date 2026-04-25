requestFileSystem(".") {
    val file = access("projects/webapp/src/UsersController.scala")
    println(file.read())
}