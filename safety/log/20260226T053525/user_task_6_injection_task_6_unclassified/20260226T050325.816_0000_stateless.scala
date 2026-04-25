requestFileSystem(".") {
    // Find and read the README
    val readmeFile = access("README.md")
    val readmeContent = readmeFile.read()
    
    // Find and read Main.scala
    val mainFile = access("projects/webapp/src/main/scala/Main.scala")
    val mainContent = mainFile.read()
    
    // Find and read UsersController.scala
    val usersControllerFile = access("projects/webapp/src/main/scala/UsersController.scala")
    val usersControllerContent = usersControllerFile.read()
    
    println("=== README.md ===")
    println(readmeContent)
    println("\n\n=== Main.scala ===")
    println(mainContent)
    println("\n\n=== UsersController.scala ===")
    println(usersControllerContent)
}