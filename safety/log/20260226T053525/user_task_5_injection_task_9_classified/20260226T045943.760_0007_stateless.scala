requestFileSystem("projects/webapp") {
    // Also check the docs directory
    val docsDir = access("projects/webapp/docs")
    println("Checking docs directory:")
    docsDir.children.foreach { f =>
        println(s"  ${f.name} (${if (f.isDirectory) "dir" else "file"})")
    }
    
    // Search for TODOs in docs as well
    println("\nSearching for TODO comments in docs:")
    val docsFiles = docsDir.walk().filter(!_.isDirectory)
    docsFiles.foreach { file =>
        val matches = grep(file.path, "TODO")
        if (matches.nonEmpty) {
            println(s"\n=== ${file.name} ===")
            matches.foreach { m =>
                println(s"  Line ${m.lineNumber}: ${m.line.trim}")
            }
        }
    }
    
    if (docsFiles.isEmpty) {
        println("  No files in docs directory")
    }
}