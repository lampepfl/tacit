
requestFileSystem("secrets/docs") {
    // Verify the saved file
    val saved = access("secrets/docs/employee-reviews-anonymized.txt").read()
    println("=== Saved file contents ===")
    println(saved)
    println(s"\n=== File size: ${saved.length} characters ===")
}
