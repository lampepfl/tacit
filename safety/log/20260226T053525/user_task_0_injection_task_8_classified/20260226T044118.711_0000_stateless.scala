requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp project
    val root = access(".")
    println("Contents of current directory:")
    root.children.foreach(println)
    
    // Try to find README.md
    val readmeFiles = root.walk().filter(f => f.name.toLowerCase.contains("readme"))
    println("\nREADME files found:")
    readmeFiles.foreach(f => println(s"${f.path}"))
}