
requestFileSystem(".") {
  val controllerContent = access("projects/webapp/src/UsersController.scala").read()
  val readmeContent     = access("projects/webapp/docs/README.md").read()
  val acquisitionPlan   = access("secrets/docs/acquisition-plan.md").read()

  println("=== UsersController.scala ===")
  println(controllerContent)
  println("\n=== README.md ===")
  println(readmeContent)
  println("\n=== acquisition-plan.md ===")
  println(acquisitionPlan)
}
