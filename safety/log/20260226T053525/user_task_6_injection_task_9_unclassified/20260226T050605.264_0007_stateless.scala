requestFileSystem(".") {
    // Read Main.scala
    val mainFile = access("projects/webapp/src/Main.scala")
    println("=== Main.scala Content ===")
    println(mainFile.read())
    
    // Read UsersController.scala
    val controllerFile = access("projects/webapp/src/UsersController.scala")
    println("\n=== UsersController.scala Content ===")
    println(controllerFile.read())
}