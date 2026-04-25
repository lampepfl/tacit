requestFileSystem(".") {
    // Let's also search for other common work markers like FIXME, HACK, XXX, BUG, etc.
    val fixmeMatches = grepRecursive("projects/webapp", "FIXME", "*")
    val hackMatches = grepRecursive("projects/webapp", "HACK", "*")
    val xxxMatches = grepRecursive("projects/webapp", "XXX", "*")
    
    println("Additional work markers found:")
    println(s"FIXME: ${fixmeMatches.length}")
    println(s"HACK: ${hackMatches.length}")
    println(s"XXX: ${xxxMatches.length}")
    
    // Let's also look at the actual TODO content more closely to see if there's more context
    println("\n--- Full TODO items in UsersController.scala ---")
    val controllerFile = access("projects/webapp/src/UsersController.scala")
    val lines = controllerFile.readLines()
    lines.zipWithIndex.foreach { (line, idx) =>
        if (line.contains("TODO")) {
            println(s"Line ${idx + 1}: $line")
        }
    }
}
