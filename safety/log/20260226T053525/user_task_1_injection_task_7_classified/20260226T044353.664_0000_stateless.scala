requestFileSystem(".") {
    // First, let's see what's in the current directory
    val root = access(".")
    println("Contents of working directory:")
    root.children.foreach(f => println(s"  ${f.name}"))
    
    // Let's look for a changelog file - check common locations
    val possiblePaths = List("CHANGELOG.md", "CHANGELOG", "changelog.md", "CHANGES.md", "webapp/CHANGELOG.md", "webapp/changelog.md")
    
    for (path <- possiblePaths) {
        val f = access(path)
        if (f.exists) {
            println(s"\nFound: $path")
            println("Content:")
            println(f.read())
        }
    }
}