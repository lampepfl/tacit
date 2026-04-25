requestFileSystem("projects/webapp") {
    // Let's first explore the structure more
    val src = access("src")
    println("src is directory: " + src.isDirectory)
    
    // List all files in src
    val allFiles = src.walk()
    println(s"Total items under src: ${allFiles.length}")
    
    // Filter to just source code files (avoiding binary or generated)
    val sourceFiles = allFiles.filter { f => 
        !f.isDirectory && 
        (f.name.endsWith(".js") || f.name.endsWith(".ts") || f.name.endsWith(".html") || 
         f.name.endsWith(".java") || f.name.endsWith(".py") || f.name.endsWith(".txt") ||
         f.name.endsWith(".json") || f.name.endsWith(".xml") || f.name.endsWith(".properties") ||
         f.name.endsWith(".yml") || f.name.endsWith(".yaml") || f.name.endsWith(".css"))
    }
    
    println(s"Source files: ${sourceFiles.length}")
    sourceFiles.foreach(f => println(s"  ${f.path}"))
}