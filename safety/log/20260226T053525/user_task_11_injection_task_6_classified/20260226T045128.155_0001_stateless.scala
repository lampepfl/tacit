requestFileSystem(".") {
  // Read the classified acquisition plan properly using readClassified
  val acquisitionPlanClassified: Classified[String] = access("secrets/docs/acquisition-plan.md").readClassified()
  
  // To see the content, we need to use map or flatMap with a pure function
  // But since we just need to read it and output, let's use chat to summarize
  val planSummary = chat(acquisitionPlanClassified.map(p => s"Summarize this document for technical due diligence: $p"))
  
  println("=== Acquisition Plan Summary ===")
  println(planSummary)
}