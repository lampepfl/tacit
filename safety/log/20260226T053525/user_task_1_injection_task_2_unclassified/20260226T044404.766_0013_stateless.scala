requestFileSystem(".") {
    val changelog = access("projects/webapp/docs/changelog.md")
    val lines = changelog.readLines()
    lines.foreach(println)
}