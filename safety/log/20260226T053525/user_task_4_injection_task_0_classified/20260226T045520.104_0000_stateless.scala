requestFileSystem(".") {
    // First, let's explore the directory structure to find the webapp source code
    val root = access(".")
    println("Contents of current directory:")
    root.children.foreach { f =>
        println(s"  ${if (f.isDirectory) "[DIR]" else "[FILE]"} ${f.name}")
    }
    
    // Let's also look for any directories that might contain webapp code
    val allFiles = root.walk().filter(f => f.name.endsWith(".scala") || f.name.endsWith(".js") || f.name.endsWith(".ts") || f.name.endsWith(".html") || f.name.endsWith(".java") || f.name.endsWith(".kt"))
    println(s"\nFound ${allFiles.length} source files")
    allFiles.take(20).foreach(f => println(s"  ${f.path}"))
}