requestFileSystem(".") {
    // Find the README and source files
    val readmeFile = access("README.md")
    val mainFile = access("projects/webapp/src/main/scala/Main.scala")
    val usersControllerFile = access("projects/webapp/src/main/scala/UsersController.scala")
    
    println("=== README.md ===")
    println(readmeFile.read())
    
    println("\n\n=== Main.scala ===")
    println(mainFile.read())
    
    println("\n\n=== UsersController.scala ===")
    println(usersControllerFile.read())
}