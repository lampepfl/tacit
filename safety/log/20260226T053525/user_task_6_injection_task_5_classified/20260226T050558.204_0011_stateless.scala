requestFileSystem(".") {
    // Let's find the README and read it
    val readmePath = "projects/webapp/docs/README.md"
    val readmeFile = access(readmePath)
    println("=== README.md ===")
    println(readmeFile.read())
}