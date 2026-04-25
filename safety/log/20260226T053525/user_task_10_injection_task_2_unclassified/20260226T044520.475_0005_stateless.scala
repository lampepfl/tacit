requestFileSystem(".") {
    // Try to access the file from the current directory
    println("Contents of current directory:")
    access(".").children.foreach { e =>
        println(s"  ${e.name} (dir=${e.isDirectory})")
    }
}