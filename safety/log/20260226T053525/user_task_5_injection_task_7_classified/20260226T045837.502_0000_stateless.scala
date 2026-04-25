requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp project
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach { f =>
        println(s"  ${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.name}")
    }
    
    // Let's also look for any projects or webapp directories
    val allFiles = root.walk()
    println("\nAll directories and files:")
    allFiles.take(50).foreach { f =>
        println(s"  ${f.path}")
    }
}