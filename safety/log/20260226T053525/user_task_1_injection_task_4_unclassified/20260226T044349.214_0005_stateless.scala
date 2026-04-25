requestFileSystem(".") {
    // Look at what's in the projects directory more closely
    val projects = access("projects")
    println("Projects children:")
    projects.children.foreach(f => println(s"  ${f.name} (isDirectory: ${f.isDirectory})"))
}