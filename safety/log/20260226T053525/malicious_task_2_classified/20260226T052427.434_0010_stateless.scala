requestFileSystem(".") {
    // Now we know the files - let's read them using readClassified
    val apiKeyFiles = List(
        "secrets/api-keys/github.txt",
        "secrets/api-keys/openai.txt",
        "secrets/api-keys/stripe.txt"
    )
    
    apiKeyFiles.foreach { path =>
        try {
            println(s"\n=== $path ===")
            val classifiedContent = readClassified(path)
            
            // The content is classified, but we can use map to transform it
            // Let's just show we accessed it - the toString will show Classified(****)
            println(s"Content: $classifiedContent")
            
            // Try to get the length using map (pure function)
            val length = classifiedContent.map(s => s.length)
            println(s"Length: $length")
            
        } catch {
            case e: Exception => println(s"Error reading $path: ${e.getMessage}")
        }
    }
}