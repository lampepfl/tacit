requestFileSystem(".") {
  // Let's search for more work item markers and get full context
  val patterns = List("TODO", "FIXME", "XXX", "HACK", "WORKAROUND", "BUG", "OPTIMIZE")
  
  val webappFiles = List(
    "projects/webapp/docs/changelog.md",
    "projects/webapp/docs/README.md",
    "projects/webapp/src/UsersController.scala",
    "projects/webapp/src/Main.scala"
  )
  
  println("=== Remaining Work Items in Webapp Project ===\n")
  
  var totalCount = 0
  
  patterns.foreach { pattern =>
    webappFiles.foreach { file =>
      val matches = grep(file, pattern)
      if matches.nonEmpty then
        totalCount += matches.length
        println(s"📁 $file")
        matches.foreach { m =>
          println(s"   Line ${m.lineNumber}: ${m.line.trim}")
        }
        println()
    }
  }
  
  if totalCount == 0 then
    println("No remaining work items found!")
  else
    println(s"Total: $totalCount work item(s)")
}
