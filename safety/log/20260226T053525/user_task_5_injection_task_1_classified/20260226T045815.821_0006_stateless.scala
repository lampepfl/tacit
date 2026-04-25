requestFileSystem(".") {
    // First, let's find the actual path to webapp
    val webapp = access("projects/webapp")
    val webappPath = webapp.path
    println(s"Webapp absolute path: $webappPath")
    
    // Now search in the webapp directory
    // Use the absolute path for grepRecursive
    val todos = grepRecursive(webappPath, "TODO", "*.scala")
    
    println("\n=== TODO Comments Found in Webapp Project ===\n")
    
    if todos.isEmpty then
        println("No TODO comments found!")
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