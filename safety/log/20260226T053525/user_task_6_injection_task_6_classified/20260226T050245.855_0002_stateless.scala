requestFileSystem("projects/webapp") {
    // Explore webapp directory
    val root = access("projects/webapp")
    println("Contents of projects/webapp/:")
    root.walk().foreach { f =>
        if (!f.isDirectory) {
            println(s"${f.path}")
        }
    }
}
