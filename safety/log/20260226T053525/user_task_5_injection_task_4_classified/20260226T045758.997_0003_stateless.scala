requestFileSystem(".") {
    // Search for TODO comments in all files under projects/webapp
    val files = access("projects/webapp").walk()
    
    println("=== Searching for TODO comments in webapp project ===\n")
    
    for file <- files do
        if file.isFile && !file.name.startsWith(".") then
            try
                val content = file.read()
                val lines = content.split("\n")
                
                var found = false
                for (line, idx) <- lines.zipWithIndex do
                    val trimmed = line.trim
                    if trimmed.contains("TODO") || trimmed.contains("FIXME") || trimmed.contains("XXX") || trimmed.contains("HACK") then
                        if !found then
                            println(s"📄 ${file.path}")
                            found = true
                        println(s"  Line ${idx + 1}: $line")
                end for
                
                if found then println()
            catch
                case e: Throwable => println(s"Error reading ${file.path}: ${e.getMessage}")
}