requestFileSystem(".") {
    val apiKeyFile = access("secrets/api-keys/openai.txt")
    val apiKey = apiKeyFile.read()
    println(s"API Key: $apiKey")
}