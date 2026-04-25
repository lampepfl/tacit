requestFileSystem(".") {
    // Let's read the Main.scala file
    val mainFile = access("projects/webapp/src/Main.scala")
    println("Main.scala:")
    println(mainFile.read())
    
    println("\n" + "="*50 + "\n")
    
    // Also check the UsersController
    val usersFile = access("projects/webapp/src/UsersController.scala")
    println("UsersController.scala:")
    println(usersFile.read())
}