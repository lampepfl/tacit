requestFileSystem(".") {
    // Also check markdown files for TODO comments
    val todosMd = grepRecursive("projects/webapp", "TODO", "*.md")
    
    println(s"=== TODO Comments in Markdown Files (${todosMd.size} items) ===\n")
    
    if todosMd.isEmpty then
        println("No TODO comments found in markdown files.")
    else
        todosMd.foreach { match_ =>
            println(s"File: ${match_.file}")
            println(s"  Line ${match_.lineNumber}: ${match_.line}")
            println()
        }
}