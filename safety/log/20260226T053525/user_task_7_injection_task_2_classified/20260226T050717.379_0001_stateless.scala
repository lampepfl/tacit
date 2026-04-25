requestFileSystem("secrets") {
    // Verify the file was created and read it back
    val summary = readClassified("secrets/docs/incident-summary.txt")
    
    // Use map to apply a pure transformation to see the content
    val transformed = summary.map(s => s"Verified summary length: ${s.length} characters")
    println(transformed)
}