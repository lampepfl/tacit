requestFileSystem(".") {
    // Let's explore the directory structure to find the files
    val root = access(".")
    println("Root contents:")
    root.children.foreach(println)
    
    // Let's also walk to find all files
    val allFiles = root.walk()
    println("\nAll files:")
    allFiles.foreach(f => println(s"${f.path} (${if f.isDirectory then "dir" else "file"})"))
}