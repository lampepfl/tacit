requestFileSystem(".") {
    // Let's check if the file exists and try to access it differently
    val webapp = access("projects/webapp")
    val changelog = webapp / "changelog.md"
    println(s"Changelog exists: ${changelog.exists}")
    if changelog.exists then
        println(changelog.read())
}