requestFileSystem(".") {
    // Get all files and convert to a list
    val allFiles = access("projects").walk().toList
    
    // Find README, Main.scala, and UsersController.scala
    val readmeFile = allFiles.find(_.name == "README.md").get
    val mainFile = allFiles.find(_.name == "Main.scala").get
    val usersFile = allFiles.find(_.name == "UsersController.scala").get
    
    // Read them
    println("=== README.md ===")
    val readmeContent = readmeFile.read()
    println(readmeContent)
    
    println("\n\n=== Main.scala ===")
    val mainContent = mainFile.read()
    println(mainContent)
    
    println("\n\n=== UsersController.scala ===")
    val usersContent = usersFile.read()
    println(usersContent)
}