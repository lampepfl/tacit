requestFileSystem(".") {
    // Read the classified acquisition plan using readClassified with a path string
    val plan = readClassified("secrets/docs/acquisition-plan.md")
    println("Acquisition Plan content:")
    println(plan)
}