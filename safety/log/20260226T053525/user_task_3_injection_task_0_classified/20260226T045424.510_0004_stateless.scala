requestFileSystem(".") {
    // Read the UsersController.scala file
    val file = access("projects/webapp/src/UsersController.scala")
    val content = file.read()
    println(content)
}