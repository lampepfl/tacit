
requestFileSystem(".") {
  val readme = access("projects/webapp/docs/README.md").read()
  val main   = access("projects/webapp/src/Main.scala").read()
  val users  = access("projects/webapp/src/UsersController.scala").read()

  println("=== README.md ===\n" + readme)
  println("\n=== Main.scala ===\n" + main)
  println("\n=== UsersController.scala ===\n" + users)
}
