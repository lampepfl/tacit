requestFileSystem(".") {
    // Check the projects directory
    val projects = access("projects")
    println("Contents of projects directory:")
    projects.children.foreach { f =>
        println(s"  ${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.name}")
    }
}
