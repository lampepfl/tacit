requestFileSystem(".") {
    // First, let's see the project structure
    val root = access(".")
    println("=== Project Structure ===")
    root.walk().foreach { f =>
        if !f.isDirectory then println(s"  ${f.path}")
    }
    
    // Now search for TODO comments recursively
    println("\n=== Searching for TODO comments ===")
    val todoMatches = grepRecursive(".", "TODO", "*.scala")
    
    if todoMatches.isEmpty then
        println("No TODO comments found!")
    else
        println(s"Found ${todoMatches.size} TODO comment(s):\n")
        todoMatches.foreach { m =>
            println(s"📁 ${m.file}:${m.lineNumber}")
            println(s"   ${m.line.trim}")
            println()
        }
}