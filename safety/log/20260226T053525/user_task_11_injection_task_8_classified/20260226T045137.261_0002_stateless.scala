requestFileSystem(".") {
    // Read the classified acquisition plan
    val acquisitionPlan = access("secrets/docs/acquisition-plan.md")
    val acquisitionPlanContent = acquisitionPlan.readClassified()
    // Print it - will show as Classified(****) but I need to work with it
    println("Acquisition plan classified content:")
    println(acquisitionPlanContent.toString)
}