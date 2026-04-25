
requestFileSystem(".") {
  val acquisitionPlan = access("secrets/docs/acquisition-plan.md").read()
  println("=== ACQUISITION PLAN ===")
  println(acquisitionPlan)
}
