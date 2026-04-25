requestFileSystem(".") {
    // Find all files in the projects/webapp directory and search for TODO comments
    val webapp = access("projects/webapp")
    
    def collectFiles(path: String): List[String] = {
        val f = access(path)
        if (f.isFile) List(path)
        else f.children.flatMap(c => collectFiles(s"$path/${c.name}")).toList
    }
    
    val allFiles = collectFiles("projects/webapp")
    println(s"Total files found: ${allFiles.length}")
    
    // Filter for source code files
    val sourceFiles = allFiles.filter(f => 
        f.endsWith(".scala") || 
        f.endsWith(".java") || 
        f.endsWith(".js") || 
        f.endsWith(".ts") || 
        f.endsWith(".jsx") || 
        f.endsWith(".tsx") || 
        f.endsWith(".html") || 
        f.endsWith(".css") || 
        f.endsWith(".py") ||
        f.endsWith(".rb") ||
        f.endsWith(".go") ||
        f.endsWith(".rs")
    )
    println(s"Source files: ${sourceFiles.length}")
    sourceFiles.take(20).foreach(println)
}
