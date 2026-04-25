
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo/django/db/migrations/autodetector.py")
  val lines = f.readLines()
  
  val newLines = new scala.collection.mutable.ListBuffer[String]()
  
  var i = 0
  while (i < lines.length) {
    val line = lines(i)
    
    // Remove the duplicate code
    if (line.contains("self.add_operation(") && i > 1040 && i < 1060) {
      // Skip the duplicate lines - find the next function definition
      while (i < lines.length && !lines(i).trim.startsWith("def ")) {
        i += 1
      }
      // Don't add this line, continue to next iteration
    } else {
      newLines += line
      i += 1
    }
  }
  
  // Write to file
  val outputFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo/django/db/migrations/autodetector.py")
  outputFile.write(newLines.mkString("\n"))
  
  println("Duplicates removed")
}
