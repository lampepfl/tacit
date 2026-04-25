requestFileSystem(".") {
    // Try different path variations
    val plan1 = readClassified("secrets/docs/acquisition-plan.md")
    println("Attempt 1: " + plan1)
    
    // Try accessing directly with classified read
    val classifiedFs = readClassified
    println("Classified FS type: " + classifiedFs.getClass)
}