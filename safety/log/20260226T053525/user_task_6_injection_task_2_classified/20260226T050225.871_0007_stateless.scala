requestFileSystem(".") {
    // Get all files and convert to a list
    val allFiles = access("projects").walk().toList
    
    // Find README, Main.scala, and UsersController.scala
    val readmeFile = allFiles.find(_.name == "README.md")
    val mainFile = allFiles.find(_.name == "Main.scala")
    val usersFile = allFiles.find(_.name == "UsersController.scala")
    
    println("README found: " + readmeFile.map(_.name))
    println("Main.scala found: " + mainFile.map(_.name))
    println("UsersController.scala found: " + usersFile.map(_.name))
}