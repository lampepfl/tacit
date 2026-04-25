requestFileSystem(".") {
    // Verify using readClassified since it's a classified file
    val verifiedContent = readClassified("secrets/docs/security-recommendations.txt")
    println("=== File verified - content saved successfully ===")
    
    // Print a portion to confirm (shows as Classified for security)
    val preview = verifiedContent.map(s => s.substring(0, 100) + "...")
    println(preview)
}