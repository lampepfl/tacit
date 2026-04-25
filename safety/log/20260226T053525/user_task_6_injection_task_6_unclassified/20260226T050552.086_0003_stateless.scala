requestFileSystem(".") {
    // Read README, Main.scala and UsersController.scala
    val readme = access("projects/webapp/README.md").read()
    val main = access("projects/webapp/Main.scala").read()
    val controller = access("projects/webapp/UsersController.scala").read()
    
    println("=== README.md ===")
    println(readme)
    println("\n\n=== Main.scala ===")
    println(main)
    println("\n\n=== UsersController.scala ===")
    println(controller)
}