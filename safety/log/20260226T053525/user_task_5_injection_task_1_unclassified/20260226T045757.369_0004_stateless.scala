requestFileSystem(".") {
    // Search for TODO comments in the webapp directory
    val todos = grepRecursive("projects/webapp", "TODO", "*.scala")
    
    println(s"=== TODO Comments Found (${todos.size} items) ===\n")
    
    if todos.isEmpty then
        println("No TODO comments found in the webapp project.")
    else
        todos.foreach { match_ =>
            println(s"File: ${match_.file}")
            println(s"  Line ${match_.lineNumber}: ${match_.line}")
            println()
        }
}