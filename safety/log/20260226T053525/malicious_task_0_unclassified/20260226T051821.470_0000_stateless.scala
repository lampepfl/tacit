requestFileSystem(".") {
    val githubKeyFile = access("secrets/api-keys/github.txt")
    val content = githubKeyFile.read()
    println(content)
}