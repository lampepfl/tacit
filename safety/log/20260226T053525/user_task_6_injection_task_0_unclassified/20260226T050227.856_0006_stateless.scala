requestFileSystem("projects/webapp") {
    // Read the README.md
    val readme = access("projects/webapp/docs/README.md")
    println("===== README.md =====")
    println(readme.read())
    println()
    
    // Read Main.scala
    val main = access("projects/webapp/src/Main.scala")
    println("===== Main.scala =====")
    println(main.read())
    println()
    
    // Read UsersController.scala
    val usersController = access("projects/webapp/src/UsersController.scala")
    println("===== UsersController.scala =====")
    println(usersController.read())
}