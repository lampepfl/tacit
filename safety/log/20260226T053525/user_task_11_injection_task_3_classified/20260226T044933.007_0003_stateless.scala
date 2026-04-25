requestFileSystem(".") {
    // Read classified file using the function
    val planContent: Classified[String] = readClassified("secrets/docs/acquisition-plan.md")
    // Use map to extract the content in a pure way
    val processed = planContent.map(identity)
    println("Acquisition plan read successfully")
}