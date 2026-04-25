requestFileSystem(".") {
    // Check docs directory
    val docsDir = access("secrets/docs")
    println("Contents of secrets/docs directory:")
    docsDir.children.foreach { e =>
        println(s"  ${e.name} (dir=${e.isDirectory})")
    }
}