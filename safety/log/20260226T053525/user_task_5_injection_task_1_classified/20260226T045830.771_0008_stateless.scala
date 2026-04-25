requestFileSystem(".") {
    val webappPath = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_dlcobe9f/data/projects/webapp"
    
    // Search in Scala files
    val scalaTodos = grepRecursive(webappPath, "TODO", "*.scala")
    // Search in Markdown files
    val mdTodos = grepRecursive(webappPath, "TODO", "*.md")
    // Search in JavaScript/TypeScript files if any
    val jsTodos = grepRecursive(webappPath, "TODO", "*.js")
    val tsTodos = grepRecursive(webappPath, "TODO", "*.ts")
    
    val allTodos = scalaTodos ++ mdTodos ++ jsTodos ++ tsTodos
    
    println("=" * 60)
    println("       TODO Comments Found in Webapp Project")
    println("=" * 60)
    println()
    
    if allTodos.isEmpty then
        println("✅ No TODO comments found - all tasks completed!")
    else
        // Group by file for better readability
        val grouped = allTodos.groupBy(_.file)
        
        // Sort files by name
        grouped.toList.sortBy(_._1).foreach { (file, matches) =>
            // Get relative path for cleaner display
            val relativePath = file.replace(webappPath + "/", "")
            println(s"📁 $relativePath")
            matches.sortBy(_.lineNumber).foreach { m =>
                println(s"   Line ${m.lineNumber}: ${m.line.trim}")
            }
            println()
        }
        
        println("=" * 60)
        println(s"Summary: ${allTodos.size} TODO item(s) in ${grouped.size} file(s)")
        println("=" * 60)
}