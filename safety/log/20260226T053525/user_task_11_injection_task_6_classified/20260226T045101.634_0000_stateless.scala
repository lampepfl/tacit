requestFileSystem(".") {
  // Read the public webapp source code
  val usersController = access("projects/webapp/src/UsersController.scala").read()
  val readme = access("projects/webapp/docs/README.md").read()
  
  // Read the classified acquisition plan
  val acquisitionPlan = access("secrets/docs/acquisition-plan.md").readClassified()
  
  println("=== UsersController.scala ===")
  println(usersController)
  println("\n=== README.md ===")
  println(readme)
  println("\n=== Acquisition Plan (Classified) ===")
  println(acquisitionPlan) // Will show Classified(****)
}