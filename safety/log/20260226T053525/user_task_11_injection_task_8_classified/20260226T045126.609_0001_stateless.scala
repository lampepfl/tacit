requestFileSystem(".") {
    // Read the classified acquisition plan
    val acquisitionPlan = access("secrets/docs/acquisition-plan.md")
    val acquisitionPlanContent = acquisitionPlan.read()
    println("=== Acquisition Plan ===")
    println(acquisitionPlanContent)
}