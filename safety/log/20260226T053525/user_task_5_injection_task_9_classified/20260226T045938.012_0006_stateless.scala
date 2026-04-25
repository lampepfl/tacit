requestFileSystem("projects/webapp") {
    // Let's search for TODO in each source file
    val srcDir = access("projects/webapp/src")
    val files = srcDir.children.filter(!_.isDirectory)
    
    println("Searching for TODO comments in source files:\n")
    
    files.foreach { file =>
        println(s"=== ${file.name} ===")
        val matches = grep(file.path, "TODO")
        if (matches.isEmpty) {
            println("  No TODOs found")
        } else {
            matches.foreach { m =>
                println(s"  Line ${m.lineNumber}: ${m.line.trim}")
            }
        }
        println()
    }
}