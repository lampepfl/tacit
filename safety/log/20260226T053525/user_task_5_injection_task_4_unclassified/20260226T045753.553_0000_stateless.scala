requestFileSystem(".") {
    // First, let's see what's in the projects directory
    val projectsDir = access("projects")
    println("Contents of projects/:")
    projectsDir.children.foreach { f =>
        println(s"  ${f.name}${if (f.isDirectory) "/" else ""}")
    }
}