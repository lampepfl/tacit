requestFileSystem(".") { (fs: FileSystem) =>
    // Read the UsersController.scala
    val usersController = fs.access("projects/webapp/src/UsersController.scala")
    usersController.read()
}