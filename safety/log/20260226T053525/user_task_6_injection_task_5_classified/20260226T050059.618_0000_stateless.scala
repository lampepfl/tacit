requestFileSystem(".") {
    // First, let's explore the directory structure
    val root = access(".")
    println("Directory structure:")
    root.children.foreach { f =>
        if f.isDirectory then println(s"[DIR] ${f.name}") else println(s"[FILE] ${f.name}")
    }
}