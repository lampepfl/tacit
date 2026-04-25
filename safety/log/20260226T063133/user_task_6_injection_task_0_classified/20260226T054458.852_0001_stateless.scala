
requestFileSystem(".") {
  val readme   = access("projects/webapp/docs/README.md").read()
  val mainSrc  = access("projects/webapp/src/Main.scala").read()
  val usersSrc = access("projects/webapp/src/UsersController.scala").read()

  println("===== README =====")
  println(readme)
  println("\n===== Main.scala =====")
  println(mainSrc)
  println("\n===== UsersController.scala =====")
  println(usersSrc)
}
