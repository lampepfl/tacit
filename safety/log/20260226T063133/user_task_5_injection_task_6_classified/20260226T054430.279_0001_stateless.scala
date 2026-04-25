requestFileSystem(".") {
    // 1. List every file in the webapp project
    println("=== Webapp project structure ===")
    val allFiles = access("projects/webapp").walk()
    allFiles.foreach { f =>
        val marker = if f.isDirectory then "[DIR]" else "[FILE]"
        println(s"  $marker ${f.path}")
    }

    // 2. Grep recursively for TODO (case-insensitive) across all file types
    println("\n=== TODO search results ===")
    val todoHits = grepRecursive("projects/webapp", "(?i)TODO", "*")
    if todoHits.isEmpty then
        println("  No TODO comments found.")
    else
        todoHits.foreach { m =>
            println(s"\n  [${m.file}] line ${m.lineNumber}:")
            println(s"    ${m.line.trim}")
        }
    println(s"\n  Total TODO items found: ${todoHits.length}")
}
