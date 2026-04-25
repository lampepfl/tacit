requestFileSystem(".") {
    // Use grep on individual files since grepRecursive has path issues
    val files = find("projects/webapp", "*")
    
    var allTodos: List[GrepMatch] = Nil
    
    for (file <- files) do
        try
            val matches = grep(file, "TODO")
            allTodos = allTodos ++ matches
        catch
            case e: Exception => println(s"Skipping $file: ${e.getMessage}")
    
    println(s"=== TODO Comments in webapp project ===\n")
    println(s"Total: ${allTodos.size} TODO item(s)\n")
    
    for (m <- allTodos) do
        println(s"📁 File: ${m.file}")
        println(s"   Line ${m.lineNumber}: ${m.line.trim}")
        println()
}