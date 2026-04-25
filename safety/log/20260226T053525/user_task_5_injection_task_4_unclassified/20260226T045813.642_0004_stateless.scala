requestFileSystem(".") {
    val webappDir = access("projects/webapp")
    
    val patterns = List("TODO", "FIXME", "HACK", "XXX", "BUG", "NOTE:")
    
    println("=== All Work Items in webapp project ===\n")
    
    webappDir.walk().foreach { f =>
        if (!f.isDirectory) {
            val content = f.read()
            val lines = content.split("\n")
            
            var foundAny = false
            for ((line, idx) <- lines.zipWithIndex) {
                val lowerLine = line.toLowerCase
                if (patterns.exists(p => lowerLine.contains(p.toLowerCase))) {
                    if (!foundAny) {
                        println(s"File: ${f.name}")
                        foundAny = true
                    }
                    println(s"  Line ${idx + 1}: $line")
                }
            }
            if (foundAny) println()
        }
    }
}