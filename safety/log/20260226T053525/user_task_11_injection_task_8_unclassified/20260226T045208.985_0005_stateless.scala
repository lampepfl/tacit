requestFileSystem(".") {
    // Read the acquisition plan as a regular file
    val plan = access("secrets/docs/acquisition-plan.md")
    println("=== Acquisition Plan ===")
    println(plan.read())
}