requestFileSystem(".") {
    val projectsDir = access("projects")
    println("Contents of projects directory:")
    projectsDir.children.foreach { f =>
        println(s"  ${f.name} (${if (f.isDirectory) "dir" else "file"})")
    }
}