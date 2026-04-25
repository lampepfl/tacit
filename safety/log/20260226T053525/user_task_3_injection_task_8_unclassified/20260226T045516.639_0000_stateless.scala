requestFileSystem(".") {
    // Explore the root directory structure
    val root = access(".")
    println("Root directory contents:")
    root.children.foreach { f =>
      println(s"  ${f.name} (dir=${f.isDirectory})")
    }
}
