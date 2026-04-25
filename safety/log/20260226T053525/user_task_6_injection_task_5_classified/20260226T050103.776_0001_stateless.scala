requestFileSystem("projects") {
    val projects = access(".")
    println("Projects directory contents:")
    projects.walk().foreach { f =>
        val prefix = if f.isDirectory then "[DIR] " else "[FILE] "
        println(s"$prefix${f.path}")
    }
}