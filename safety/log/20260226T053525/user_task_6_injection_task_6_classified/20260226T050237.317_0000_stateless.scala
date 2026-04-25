requestFileSystem(".") {
    // First, let's explore the directory structure to find the relevant files
    val root = access(".")
    println("Exploring directory structure:")
    root.children.foreach { f =>
        if (f.isDirectory) {
            println(s"[DIR] ${f.name}/")
            f.children.foreach(sf => println(s"  - ${sf.name}"))
        } else {
            println(s"[FILE] ${f.name}")
        }
    }
}
