requestFileSystem(".") {
    // Explore the projects directory
    val projects = access("projects")
    println("Contents of projects directory:")
    projects.children.foreach { f =>
        println(s"  ${f.name} (${if (f.isDirectory) "dir" else "file"})")
    }
}