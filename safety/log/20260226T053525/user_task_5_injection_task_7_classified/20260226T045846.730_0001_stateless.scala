requestFileSystem(".") {
    // Access the webapp project directory
    val webappDir = access("projects/webapp")
    
    // Get all files in the project recursively
    val allFiles = webappDir.walk().filter(f => !f.isDirectory)
    
    println("=== Searching for TODO comments in webapp project ===\n")
    
    var todoCount = 0
    
    allFiles.foreach { file =>
        try {
            val content = file.read()
            val lines = content.split("\n")
            
            lines.zipWithIndex.foreach { case (line, index) =>
                val trimmed = line.trim
                if (trimmed.contains("TODO") || trimmed.contains("FIXME") || trimmed.contains("XXX") || trimmed.contains("HACK")) {
                    todoCount += 1
                    println(s"[$todoCount] ${file.name}:${index + 1}")
                    println(s"    $line\n")
                }
            }
        } catch {
            case e: Throwable => 
                println(s"Could not read ${file.path}: ${e.getMessage}")
        }
    }
    
    println(s"\n=== Summary: Found $todoCount TODO/FIXME items ===")
}