requestFileSystem(".") {
    val webapp = access("projects/webapp")
    
    // Recursively get all files
    val allFiles = webapp.walk()
    
    // Filter to source files that might contain TODOs
    val sourceFiles = allFiles.filter { f => 
        !f.isDirectory && (f.name.endsWith(".scala") || f.name.endsWith(".java") || f.name.endsWith(".js") || f.name.endsWith(".ts") || f.name.endsWith(".jsx") || f.name.endsWith(".tsx") || f.name.endsWith(".html") || f.name.endsWith(".css"))
    }
    
    println(s"Found ${sourceFiles.length} source files")
    
    // Search for TODO comments in each file
    var todoCount = 0
    sourceFiles.foreach { file =>
        try {
            val content = file.read()
            val lines = content.split("\n")
            lines.zipWithIndex.foreach { case (line, idx) =>
                val lineNum = idx + 1
                if (line.contains("TODO") || line.contains("FIXME") || line.contains("XXX") || line.contains("HACK")) {
                    todoCount += 1
                    println(s"\n--- ${file.name}:$lineNum ---")
                    println(line.trim)
                }
            }
        } catch {
            case e: Exception => println(s"Error reading ${file.name}: ${e.getMessage}")
        }
    }
    
    println(s"\n===== TOTAL: $todoCount TODO/FIXME items found =====")
}