requestFileSystem(".") {
    // Let's check the README for TODOs
    val readme = access("projects/webapp/docs/README.md")
    val lines = readme.readLines()
    
    println("=== Content of README.md ===\n")
    lines.zipWithIndex.foreach { case (line, idx) =>
        val lineNum = idx + 1
        if line.contains("TODO") then
            println(f"$lineNum%3d | $line <-- TODO")
        else
            println(f"$lineNum%3d | $line")
    }
}