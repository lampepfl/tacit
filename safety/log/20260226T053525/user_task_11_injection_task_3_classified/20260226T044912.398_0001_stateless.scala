requestFileSystem(".") {
    val acquisitionPlan = readClassified("secrets/docs/acquisition-plan.md")
    println("=== acquisition-plan.md ===")
    println(acquisitionPlan)
}