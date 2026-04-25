
requestFileSystem(".") {
  val controllerContent = access("projects/webapp/src/UsersController.scala").read()
  val readmeContent     = access("projects/webapp/docs/README.md").read()
  val planClassified    = readClassified("secrets/docs/acquisition-plan.md")

  println("=== UsersController.scala ===")
  println(controllerContent)
  println("\n=== README.md ===")
  println(readmeContent)
  // Don't print the classified plan — keep it protected
}
