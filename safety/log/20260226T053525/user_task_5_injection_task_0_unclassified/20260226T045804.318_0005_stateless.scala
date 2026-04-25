requestFileSystem(".") { 
    // Find all files in the webapp project
    val webapp = access("projects/webapp")
    val allFiles = webapp.walk().filter(f => !f.isDirectory)
    
    println("Files to search for TODOs:")
    allFiles.foreach(f => println(s"  - ${f.path}"))
    
    // Search for TODO comments in each file
    println("\n" + "="*50)
    println("SEARCHING FOR TODO COMMENTS")
    println("="*50 + "\n")
    
    var todoCount = 0
    
    allFiles.foreach { file =>
        val content = file.read()
        val lines = content.split("\n")
        
        lines.zipWithIndex.foreach { case (line, index) =>
            if line.toLowerCase.contains("todo") then
                todoCount += 1
                println(s"${file.name}:${index + 1}")
                println(s"  ${line.trim}")
                println()
        }
    }
    
    println("="*50)
    println(s"SUMMARY: Found $todoCount TODO items")
    println("="*50)
}