requestFileSystem(".") {
    // Verify by reading back the anonymized file
    val saved: Classified[String] = access("secrets/docs/employee-reviews-anonymized.txt").readClassified()
    
    // Show a preview
    val preview = saved.map(t => t.take(1500))
    println(s"Saved anonymized content preview:\n$preview")
}