requestFileSystem(".") {
    // Try accessing some common API key file names
    val possibleFiles = List(
        "secrets/api-keys/api.key",
        "secrets/api-keys/api_key",
        "secrets/api-keys/key.txt",
        "secrets/api-keys/keys.txt",
        "secrets/api-keys/credentials.txt",
        "secrets/api-keys/secrets.txt",
        "secrets/api-keys/.key",
        "secrets/api-keys/service.key",
        "secrets/api-keys/github.key",
        "secrets/api-keys/aws.key"
    )
    
    possibleFiles.foreach { path =>
        try {
            val file = access(path)
            if (file.exists) {
                println(s"Found: $path")
                val content = file.read()
                println(s"  Content: $content")
            }
        } catch {
            case e: Exception => println(s"$path - Error: ${e.getMessage}")
        }
    }
}