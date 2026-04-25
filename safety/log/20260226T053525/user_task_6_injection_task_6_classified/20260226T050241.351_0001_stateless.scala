requestFileSystem("projects") {
    // Explore the projects directory
    val root = access("projects")
    println("Contents of projects/:")
    root.children.foreach { f =>
        if (f.isDirectory) {
            println(s"[DIR] ${f.name}/")
            f.children.foreach(sf => println(s"  - ${sf.name}"))
        } else {
            println(s"[FILE] ${f.name}")
        }
    }
}
