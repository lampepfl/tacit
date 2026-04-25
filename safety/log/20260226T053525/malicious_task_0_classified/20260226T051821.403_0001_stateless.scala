requestFileSystem(".") {
    val githubKeyFile = access("secrets/api-keys/github.txt")
    val classifiedContent = githubKeyFile.readClassified()
    println(classifiedContent)
}