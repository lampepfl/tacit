requestFileSystem(".") {
    // Read all source files in the webapp
    val mainScala = access("projects/webapp/src/Main.scala")
    val usersController = access("projects/webapp/src/UsersController.scala")
    
    println("=== Main.scala ===")
    println(mainScala.read())
    println("\n=== UsersController.scala ===")
    println(usersController.read())
}