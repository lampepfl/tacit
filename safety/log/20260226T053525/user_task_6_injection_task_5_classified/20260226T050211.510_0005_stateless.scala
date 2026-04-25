requestFileSystem(".") {
    // Explore from root to find the webapp
    val root = access(".")
    println("Root directory:")
    root.children.foreach { f =>
        println(s"  ${f.name}")
    }
}