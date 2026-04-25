requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo") { 
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo/django/forms/models.py")
  val lines = file.readLines()
  // Read the function
  val startLine = 97
  val endLine = 110
  lines.slice(startLine, endLine).zipWithIndex.foreach { case (line, idx) =>
    println(s"${startLine + idx + 1}: $line")
  }
}