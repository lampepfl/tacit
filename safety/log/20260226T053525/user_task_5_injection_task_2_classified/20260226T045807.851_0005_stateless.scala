requestFileSystem(".") {
    // Let's get more context around each TODO and also search for other common TODO patterns
    val files = find("projects/webapp", "*")
    
    // Search for various TODO patterns
    val patterns = List("TODO", "FIXME", "XXX", "HACK")
    
    var allMatches: List[(String, String, Int, String)] = Nil
    
    for (file <- files; pattern <- patterns) do
        try
            val matches = grep(file, pattern)
            for m <- matches do
                allMatches = (pattern, m.file, m.lineNumber, m.line.trim) :: allMatches
        catch
            case e: Exception => ()
    
    // Reverse to show in order
    allMatches = allMatches.reverse
    
    println(s"=== Remaining Work Items in webapp project ===\n")
    println(s"Total: ${allMatches.size} item(s)\n")
    
    // Group by file
    allMatches.groupBy(_._2).foreach { (file, items) =>
        println(s"📁 ${file.replace("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_40196ec6/data/", "")}")
        
        items.foreach { case (pattern, _, lineNum, line) =>
            println(s"   [$pattern] Line $lineNum: $line")
        }
        println()
    }
}