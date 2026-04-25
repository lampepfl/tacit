requestFileSystem(".") { fs =>
    // Let's explore the projects directory to find the webapp changelog
    val projects = fs.access("projects")
    val children = projects.children.map(_.name)
    println(s"Contents of projects: $children")
}