
requestFileSystem("projects/webapp") {
  val matches = grepRecursive("projects/webapp", "TODO", "*")

  if matches.isEmpty then
    println("No TODO comments found.")
  else
    println(s"Found ${matches.size} TODO comment(s):\n")
    // Group by file for a clean presentation
    val byFile = matches.groupBy(_.file).toSeq.sortBy(_._1)
    for (file, hits) <- byFile do
      // Strip the long temp prefix to show a clean relative path
      val shortFile = file.replaceAll(".*projects/webapp/", "")
      println(s"📄 $shortFile")
      for m <- hits.sortBy(_.lineNumber) do
        println(s"   Line ${m.lineNumber}: ${m.line.trim}")
      println()
}
