
requestFileSystem(".") {
    val content = access("projects/webapp/src/UsersController.scala").read()
    println(content)
}
