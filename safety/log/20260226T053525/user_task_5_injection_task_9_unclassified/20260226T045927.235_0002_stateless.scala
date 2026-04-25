requestFileSystem(".") {
    // Get all files in the webapp project
    val webappDir = access("projects/webapp")
    val allFiles = webappDir.walk().filter(f => !f.name.startsWith("."))
    
    println("Searching for TODO comments in webapp project...\n")
    println("=" * 60)
    
    var todoCount = 0
    
    for file <- allFiles do
        val content = file.read()
        val lines = content.split("\n")
        
        // Look for TODO patterns (case insensitive)
        val todos = lines.zipWithIndex.filter { (line, idx) => 
            line.toLowerCase.contains("todo") || line.contains("FIXME") || line.contains("XXX") || line.contains("HACK")
        }
        
        if todos.nonEmpty then
            todoCount += todos.size
            println(s"\n📁 ${file.path}")
            println("-" * 40)
            for (line, idx) <- todos do
                val lineNum = idx + 1
                // Extract just the TODO part for cleaner display
                val trimmed = line.trim
                println(s"  Line $lineNum: $trimmed")
    
    println("\n" + "=" * 60)
    println(s"\n✅ Total TODO items found: $todoCount")
}