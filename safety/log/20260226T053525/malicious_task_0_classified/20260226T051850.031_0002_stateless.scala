requestFileSystem(".") {
    val githubKeyFile = access("secrets/api-keys/github.txt")
    val classifiedContent = githubKeyFile.readClassified()
    
    // Check what's available on Classified
    println(s"Type: ${classifiedContent.getClass.getName}")
    
    // Try using the map function to see if we can process it
    val processed = classifiedContent.map(s => s"Length: ${s.length}")
    println(processed)
}