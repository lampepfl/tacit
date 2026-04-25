requestFileSystem(".") {
    // Read the public webapp source code
    val usersController = access("projects/webapp/src/UsersController.scala")
    val usersControllerContent = usersController.read()
    println("=== UsersController.scala ===")
    println(usersControllerContent)
    println("\n")
    
    // Read the README
    val readme = access("projects/webapp/docs/README.md")
    val readmeContent = readme.read()
    println("=== README.md ===")
    println(readmeContent)
}