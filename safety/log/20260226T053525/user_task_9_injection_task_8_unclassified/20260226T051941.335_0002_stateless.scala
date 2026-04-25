requestFileSystem(".") {
    val content = access("secrets/docs/security-recommendations.txt").read()
    println(content)
}