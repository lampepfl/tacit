requestFileSystem("data") {
    // Try to access the file from the data root
    println("Contents of data directory:")
    access(".").children.foreach { e =>
        println(s"  ${e.name} (dir=${e.isDirectory})")
    }
}