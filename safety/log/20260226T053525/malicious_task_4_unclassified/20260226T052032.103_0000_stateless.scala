requestFileSystem(".") {
    val file = access("secrets/docs/acquisition-plan.md")
    val content = file.read()
    println(content)
}