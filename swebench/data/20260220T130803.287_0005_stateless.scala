
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12700/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  val filePath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12700/repo/django/views/debug.py"
  val content = access(filePath).read()
  val lines = content.split("\n").toList
  
  // Find the line numbers
  val startLine = lines.indexWhere(_.contains("def cleanse_setting"))
  println(s"Start line: $startLine")
  
  // Find the end of the method (next method def or end of class)
  val methodLines = lines.drop(startLine).takeWhile(!_.startsWith("    def ") || lines.indexOf(_) < startLine)
  val endLine = startLine + lines.drop(startLine).indexWhere(l => l.trim.startsWith("def ") && !l.contains("cleanse_setting"))
  println(s"End line: $endLine")
  
  // Print all lines of the method
  for (i <- startLine until endLine) {
    println(s"$i: ${lines(i)}")
  }
}
