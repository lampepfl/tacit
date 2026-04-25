requestFileSystem(".") {
    // Let's also search for other common TODO-like markers
    val fixmeMatches = grepRecursive("projects/webapp", "FIXME", "*")
    val hackMatches = grepRecursive("projects/webapp", "HACK", "*")
    val xxxMatches = grepRecursive("projects/webapp", "XXX", "*")
    val todoMatches = grepRecursive("projects/webapp", "TODO", "*")
    
    println("=== Remaining Work Items in Webapp Project ===\n")
    
    // TODO items
    if todoMatches.nonEmpty then
        println("📋 TODO Items:")
        todoMatches.foreach { m =>
            println(s"  • ${m.file.split("/").last}:${m.lineNumber}")
            println(s"    ${m.line.trim}")
            println()
        }
    
    // FIXME items
    if fixmeMatches.nonEmpty then
        println("⚠️  FIXME Items:")
        fixmeMatches.foreach { m =>
            println(s"  • ${m.file.split("/").last}:${m.lineNumber}")
            println(s"    ${m.line.trim}")
            println()
        }
    
    // HACK items
    if hackMatches.nonEmpty then
        println("🔧 HACK Items:")
        hackMatches.foreach { m =>
            println(s"  • ${m.file.split("/").last}:${m.lineNumber}")
            println(s"    ${m.line.trim}")
            println()
        }
    
    // XXX items
    if xxxMatches.nonEmpty then
        println("❓ XXX Items:")
        xxxMatches.foreach { m =>
            println(s"  • ${m.file.split("/").last}:${m.lineNumber}")
            println(s"    ${m.line.trim}")
            println()
        }
    
    // Summary
    val total = todoMatches.length + fixmeMatches.length + hackMatches.length + xxxMatches.length
    println("=" * 50)
    println(s"Total remaining work items: $total")
}
