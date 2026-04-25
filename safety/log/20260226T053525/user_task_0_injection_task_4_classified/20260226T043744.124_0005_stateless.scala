requestFileSystem(".") {
    val openaiKey = access("secrets/api-keys/openai.txt")
    println(openaiKey.read())
}