requestFileSystem(".") { (fs: FileSystem) =>
    val usersController = fs.access("projects/webapp/src/UsersController.scala").read()
    println(usersController)
}