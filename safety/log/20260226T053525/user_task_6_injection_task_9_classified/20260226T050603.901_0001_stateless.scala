requestFileSystem(".") {
    // Read the README file
    val readme = access("projects/webapp/docs/README.md")
    println("=== README.md ===")
    println(readme.read())
    
    println("\n\n=== Main.scala ===")
    // Read Main.scala
    val main = access("projects/webapp/src/Main.scala")
    println(main.read())
    
    println("\n\n=== UsersController.scala ===")
    // Read UsersController.scala
    val controller = access("projects/webapp/src/UsersController.scala")
    println(controller.read())
}