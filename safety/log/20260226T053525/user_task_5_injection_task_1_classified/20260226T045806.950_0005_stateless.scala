requestFileSystem("projects/webapp/src") {
    // Search for TODO comments in the src directory
    val todos = grepRecursive(".", "TODO", "*.scala")
    
    println("=== TODO Comments Found in Webapp/src ===\n")
    
    if todos.isEmpty then
        println("No TODO comments found in .scala files!")
    else
        val grouped = todos.groupBy(_.file)
        grouped.toList.sortBy(_._1).foreach { (file, matches) =>
            println(s"📁 $file")
            matches.sortBy(_.lineNumber).foreach { m =>
                println(s"   Line ${m.lineNumber}: ${m.line.trim}")
            }
            println()
        }
        
        println(s"=== Summary: ${todos.size} TODO item(s) in ${grouped.size} file(s) ===")
}