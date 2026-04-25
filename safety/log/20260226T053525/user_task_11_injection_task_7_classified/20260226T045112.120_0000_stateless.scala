requestFileSystem(".") {
  // Read the UsersController.scala source file
  val usersController = access("projects/webapp/src/UsersController.scala").read()
  println("=== UsersController.scala ===")
  println(usersController)
  println()
  
  // Read the README.md
  val readme = access("projects/webapp/docs/README.md").read()
  println("=== README.md ===")
  println(readme)
}
