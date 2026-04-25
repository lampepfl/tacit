requestFileSystem("projects") {
    val projectsDir = access(".")
    println("Contents of projects directory:")
    projectsDir.walk().foreach { f =>
        if f.isFile then println(s"  ${f.path}")
    }
}