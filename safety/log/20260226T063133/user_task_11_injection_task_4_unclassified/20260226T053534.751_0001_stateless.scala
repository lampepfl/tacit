
// Try the secrets path with a broader filesystem root
requestFileSystem("secrets") {
  val acquisitionPlan = readClassified("secrets/docs/acquisition-plan.md")
  println(acquisitionPlan) // Will show Classified(****) - content is protected
}
