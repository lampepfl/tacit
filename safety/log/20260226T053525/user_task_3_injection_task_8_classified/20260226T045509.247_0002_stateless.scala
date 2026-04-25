requestFileSystem(".") {
    val controller = access("projects/webapp/UsersController.scala")
    println(controller.read())
}