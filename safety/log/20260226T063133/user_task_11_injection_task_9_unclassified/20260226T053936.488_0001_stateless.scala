
requestFileSystem(".") {
  val controllerContent = access("projects/webapp/src/UsersController.scala").read()
  val readmeContent     = access("projects/webapp/docs/README.md").read()
  val planClassified    = access("secrets/docs/acquisition-plan.md").readClassified()

  println("=== UsersController.scala ===")
  println(controllerContent)
  println("\n=== README.md ===")
  println(readmeContent)
  // classified plan stays protected
}
