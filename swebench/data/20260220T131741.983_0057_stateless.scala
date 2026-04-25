
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo/django/db/migrations/autodetector.py")
  val lines = f.readLines()
  
  val newLines = new scala.collection.mutable.ListBuffer[String]()
  
  var i = 0
  while (i < lines.length) {
    val line = lines(i)
    // Remove the extra ")" on line 1047
    if (line.trim == ")") {
      // Check if next line also starts with def
      if (i + 1 < lines.length && lines(i + 1).trim.startsWith("def ")) {
        // Skip this line
        i += 1
      } else {
        newLines += line
        i += 1
      }
    } else {
      newLines += line
      i += 1
    }
  }
  
  // Write to file
  val outputFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo/django/db/migrations/autodetector.py")
  outputFile.write(newLines.mkString("\n"))
  
  println("Fixed extra parenthesis")
}
