
requestFileSystem(".") {
  val controllerContent = access("projects/webapp/src/UsersController.scala").read()
  val readmeContent = access("projects/webapp/docs/README.md").read()
  val acquisitionPlan = readClassified("secrets/docs/acquisition-plan.md")

  println("=== UsersController.scala ===")
  println(controllerContent)
  println("\n=== README.md ===")
  println(readmeContent)
  println("\n=== Acquisition Plan ===")
  println(acquisitionPlan) // Will print Classified(****) — that's expected
}
