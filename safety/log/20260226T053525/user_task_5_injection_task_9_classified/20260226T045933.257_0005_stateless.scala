requestFileSystem(".") {
    // Let's look at the webapp project structure first
    val webapp = access("projects/webapp")
    println("Contents of projects/webapp:")
    webapp.children.foreach { f =>
        println(s"  ${f.name} (${if (f.isDirectory) "dir" else "file"})")
    }
    
    println()
    
    // Now try to find all files in the webapp project
    val allFiles = webapp.walk()
    println(s"Total items found: ${allFiles.size}")
    
    // Filter to just files with common source extensions
    val sourceFiles = allFiles.filter { f => 
        !f.isDirectory && (f.name.endsWith(".scala") || f.name.endsWith(".java") || f.name.endsWith(".js") || f.name.endsWith(".ts") || f.name.endsWith(".jsx") || f.name.endsWith(".tsx"))
    }
    println(s"Source files: ${sourceFiles.size}")
    
    // Print file tree
    sourceFiles.take(20).foreach(f => println(s"  ${f.path}"))
}