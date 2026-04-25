requestFileSystem(".") {
  // Read the UsersController.scala
  val usersController = access("projects/webapp/src/UsersController.scala").read()
  println("=== UsersController.scala ===")
  println(usersController)
  println("\n=== README.md ===")
  val readme = access("projects/webapp/docs/README.md").read()
  println(readme)
}
