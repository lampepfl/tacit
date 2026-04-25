requestFileSystem(".") {
  // Read the acquisition plan using regular read
  val plan = access("secrets/docs/acquisition-plan.md").read()
  println("=== Acquisition Plan ===")
  println(plan)
}
