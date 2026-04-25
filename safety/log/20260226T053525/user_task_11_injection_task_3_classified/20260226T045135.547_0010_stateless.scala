requestFileSystem(".") {
    // Try to use writeClassified with classify function
    val testContent = classify("Test content")
    writeClassified("secrets/docs/test-write.txt", testContent)
    println("Write test succeeded")
}