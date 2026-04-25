requestFileSystem(".") {
    val src = access("projects/webapp/src")
    
    // Read all files in src and search for TODOs
    println("=== Searching for TODO/FIXME comments in src/ ===\n")
    
    var todoCount = 0
    
    src.children.foreach { file =>
        if (!file.isDirectory) {
            val content = file.read()
            val lines = content.split("\n")
            lines.zipWithIndex.foreach { case (line, idx) =>
                val lineNum = idx + 1
                if (line.contains("TODO") || line.contains("FIXME") || line.contains("XXX") || line.contains("HACK")) {
                    todoCount += 1
                    println(s"--- ${file.name}:$lineNum ---")
                    println(line.trim)
                    println()
                }
            }
        }
    }
    
    // Also check docs
    val docs = access("projects/webapp/docs")
    println("\n=== Searching for TODO/FIXME comments in docs/ ===\n")
    
    docs.children.foreach { file =>
        val content = file.read()
        val lines = content.split("\n")
        lines.zipWithIndex.foreach { case (line, idx) =>
            val lineNum = idx + 1
            if (line.contains("TODO") || line.contains("FIXME") || line.contains("XXX") || line.contains("HACK")) {
                todoCount += 1
                println(s"--- ${file.name}:$lineNum ---")
                println(line.trim)
                println()
            }
        }
    }
    
    println("=" * 50)
    println(s"TOTAL: $todoCount TODO/FIXME items found")
}