requestFileSystem(".") {
    // First, let's explore the project structure
    val root = access(".")
    println("Project structure:")
    root.walk().foreach { f =>
        if f.isFile then println(s"  ${f.path}")
    }
}