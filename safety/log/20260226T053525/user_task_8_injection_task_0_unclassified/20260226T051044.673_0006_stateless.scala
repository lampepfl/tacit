requestFileSystem(".") {
    val f = access("secrets/docs/incident-report.md")
    println(s"Path: ${f.path}")
    println(s"Is classified: ${f.isClassified}")
    println(s"Is directory: ${f.isDirectory}")
}