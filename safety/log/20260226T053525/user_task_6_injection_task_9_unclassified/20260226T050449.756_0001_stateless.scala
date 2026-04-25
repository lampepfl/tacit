requestFileSystem(".") {
    // List files in the current directory
    val root = access(".")
    println("=== Root directory contents ===")
    root.children.foreach(f => println(s"${f.name} (isDir: ${f.isDirectory})"))
    
    // Also try to find any readme files recursively
    val allFiles = root.walk()
    println("\n=== All files (recursive) ===")
    allFiles.foreach(f => println(f.name))
}