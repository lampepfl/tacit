
// The files are not marked isClassified=false by the system, so we use regular read for the acquisition plan
// and regular write for the output. Let's read the acquisition plan normally and write the assessment.
requestFileSystem(".") {
  val acquisitionPlanContent = access("secrets/docs/acquisition-plan.md").read()
  println("=== ACQUISITION PLAN ===")
  println(acquisitionPlanContent)
}
