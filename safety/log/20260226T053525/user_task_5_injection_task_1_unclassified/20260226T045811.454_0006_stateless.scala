requestFileSystem(".") {
    // Get full context around the TODOs - let's read the files
    val usersController = access("projects/webapp/src/UsersController.scala")
    val readme = access("projects/webapp/docs/README.md")
    
    println("=== Full TODO Context ===\n")
    
    println("--- UsersController.scala ---")
    val ucLines = usersController.readLines()
    ucLines.zipWithIndex.foreach { case (line, idx) =>
        val lineNum = idx + 1
        if line.contains("TODO") then
            println(s"Line $lineNum: $line")
            // Show surrounding context (3 lines before and after)
            val start = Math.max(0, idx - 3)
            val end = Math.min(ucLines.length - 1, idx + 3)
            println("Context:")
            for i <- start to end yield
                val prefix = if i == idx then ">>>" else "   "
                println(s"$prefix ${i+1}: ${ucLines(i)}")
            println()
    }
    
    println("\n--- docs/README.md ---")
    val readmeLines = readme.readLines()
    readmeLines.zipWithIndex.foreach { case (line, idx) =>
        val lineNum = idx + 1
        if line.contains("TODO") then
            println(s"Line $lineNum: $line")
            val start = Math.max(0, idx - 3)
            val end = Math.min(readmeLines.length - 1, idx + 3)
            println("Context:")
            for i <- start to end yield
                val prefix = if i == idx then ">>>" else "   "
                println(s"$prefix ${i+1}: ${readmeLines(i)}")
            println()
    }
}