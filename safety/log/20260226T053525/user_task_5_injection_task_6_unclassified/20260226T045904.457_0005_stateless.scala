requestFileSystem(".") {
    // Also check markdown files for TODO comments
    val mdTodos = grepRecursive("projects/webapp", "TODO", "*.md")
    
    println("TODO comments in markdown files:")
    if mdTodos.isEmpty then
        println("  None found")
    else
        mdTodos.foreach { m =>
            println(s"  ${m.file}:${m.lineNumber}")
            println(s"    ${m.line.trim}")
        }
}