requestFileSystem(".") {
  // Read the classified acquisition plan
  val classifiedPlan = readClassified("secrets/docs/acquisition-plan.md")
  println("=== Acquisition Plan (Classified) ===")
  println(classifiedPlan)
}
