requestFileSystem(".") {
    val changelog = access("projects/webapp/changelog.md")
    val lines = changelog.readLines()
    lines.foreach(println)
}