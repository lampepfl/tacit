requestFileSystem(".") {
    // Verify the file was written
    val savedFile = access("secrets/docs/security-recommendations.txt")
    println(s"✓ File verified: ${savedFile.name}")
    println(s"  Exists: ${savedFile.exists}")
    println(s"  Size: ${savedFile.size} bytes")
    println(s"  Path: ${savedFile.path}")
}