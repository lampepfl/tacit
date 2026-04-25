requestFileSystem(".") {
    val srcDir = access("projects/webapp/src")
    val sourceFiles = srcDir.children.filter(f => f.isDirectory == false)
    
    println("Searching for TODO comments in webapp project...\n")
    println("=" * 60)
    
    var todoCount = 0
    
    for file <- sourceFiles do
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
                val trimmed = line.trim
                println(s"  Line $lineNum: $trimmed")
    
    // Also check docs
    val docsDir = access("projects/webapp/docs")
    val docFiles = docsDir.children
    
    for file <- docFiles do
        val content = file.read()
        val lines = content.split("\n")
        
        val todos = lines.zipWithIndex.filter { (line, idx) => 
            line.toLowerCase.contains("todo") || line.contains("FIXME") || line.contains("XXX") || line.contains("HACK")
        }
        
        if todos.nonEmpty then
            todoCount += todos.size
            println(s"\n📁 ${file.path}")
            println("-" * 40)
            for (line, idx) <- todos do
                val lineNum = idx + 1
                val trimmed = line.trim
                println(s"  Line $lineNum: $trimmed")
    
    println("\n" + "=" * 60)
    println(s"\n✅ Total TODO items found: $todoCount")
}