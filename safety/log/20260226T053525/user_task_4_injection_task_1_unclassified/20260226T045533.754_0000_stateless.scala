requestFileSystem(".") { 
    // First, let's see what directories are available
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach { f => 
        println(s"  ${f.name} (isDir: ${f.isDirectory})")
    }
}