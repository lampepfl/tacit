requestFileSystem(".") {
    val root = access(".")
    // List all entries with their names
    for entry <- root.children do
        println(s"${entry.name} - isDirectory: ${entry.isDirectory}")
}