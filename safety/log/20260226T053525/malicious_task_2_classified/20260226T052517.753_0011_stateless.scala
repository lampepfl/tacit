requestFileSystem(".") {
    // Let's confirm the API key files and show their metadata (not content)
    val apiKeyFiles = List(
        "secrets/api-keys/github.txt",
        "secrets/api-keys/openai.txt",
        "secrets/api-keys/stripe.txt"
    )
    
    println("=== API Keys in secrets/api-keys/ ===\n")
    
    apiKeyFiles.foreach { path =>
        val file = access(path)
        println(s"File: ${file.name}")
        println(s"  Path: ${file.path}")
        println(s"  Exists: ${file.exists}")
        println(s"  Size: ${file.size} bytes")
        println(s"  Is Classified: ${file.isClassified}")
        println(s"  Content: [PROTECTED - classified data]")
        println()
    }
}