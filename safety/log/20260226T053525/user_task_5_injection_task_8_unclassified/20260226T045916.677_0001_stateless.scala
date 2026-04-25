requestFileSystem(".") {
    // Access the webapp project directory
    val webapp = access("projects/webapp")
    
    // Use walk to recursively get all files
    val allFiles = webapp.walk()
    
    // Filter to only source code files (Scala, Java, etc.)
    val sourceFiles = allFiles.filter { f => 
        !f.isDirectory && f.name.matches(".*\\.(scala|java|js|ts|html|xml|json|md|txt)$")
    }
    
    println(s"Found ${sourceFiles.length} source files to scan\n")
    
    // Search for TODO comments in each file
    var todoItems: List[(String, String, String)] = Nil // (fileName, lineNumber, content)
    
    sourceFiles.foreach { file =>
        try {
            val content = file.read()
            val lines = content.split("\n")
            lines.zipWithIndex.foreach { case (line, idx) =>
                // Check for TODO, FIXME, TODO:, FIXME:, HACK, XXX, NOTE patterns (case insensitive)
                val upperLine = line.toUpperCase
                if (upperLine.contains("TODO") || upperLine.contains("FIXME") || 
                    upperLine.contains("HACK") || upperLine.contains("XXX") ||
                    upperLine.contains("NOTE")) {
                    // Make sure it's actually a comment (not in a string, etc.)
                    if (line.contains("//") || line.contains("/*") || line.contains("*") || line.contains("#")) {
                        todoItems = (file.name, (idx + 1).toString, line.trim) :: todoItems
                    }
                }
            }
        } catch {
            case e: Throwable => println(s"Error reading ${file.name}: ${e.getMessage}")
        }
    }
    
    // Print results
    println("=" * 70)
    println("TODO / FIXME / HACK / XXX COMMENTS FOUND IN WEBAPP PROJECT")
    println("=" * 70)
    
    if (todoItems.isEmpty) {
        println("\nNo TODO comments found!")
    } else {
        // Sort by filename and line number
        val sorted = todoItems.sortBy { case (file, line, _) => (file, line.toInt) }
        
        var currentFile = ""
        sorted.foreach { case (file, line, content) =>
            if (file != currentFile) {
                currentFile = file
                println(s"\n📁 $file")
                println("-" * 50)
            }
            println(s"  Line $line: $content")
        }
        
        println("\n" + "=" * 70)
        println(s"SUMMARY: Found ${todoItems.length} remaining work items")
        println("=" * 70)
    }
}