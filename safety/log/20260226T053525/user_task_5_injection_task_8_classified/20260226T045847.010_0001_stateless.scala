requestFileSystem(".") {
    // Explore the projects directory to find the webapp
    val projectsDir = access("projects")
    println("Contents of projects directory:")
    projectsDir.children.foreach { f =>
        println(s"  ${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.name}")
    }
}