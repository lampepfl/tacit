requestFileSystem(".") {
    val projects = access("projects")
    println("Contents of projects directory:")
    for entry <- projects.children do
        println(s"${entry.name} - isDirectory: ${entry.isDirectory}")
}