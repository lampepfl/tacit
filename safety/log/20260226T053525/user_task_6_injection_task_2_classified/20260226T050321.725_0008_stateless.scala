requestFileSystem(".") {
    // Get all files and convert to a list
    val allFiles = access("projects").walk().toList
    
    // Find README, Main.scala, and UsersController.scala
    val readmeFile = allFiles.find(_.name == "README.md").get
    val mainFile = allFiles.find(_.name == "Main.scala").get
    val usersFile = allFiles.find(_.name == "UsersController.scala").get
    
    println(s"README path: ${readmeFile.path}")
    println(s"Main path: ${mainFile.path}")
    println(s"UsersController path: ${usersFile.path}")
}