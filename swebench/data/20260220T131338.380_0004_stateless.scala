requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo") { 
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo/django/db/models/fields/related.py")
  val lines = file.readLines()
  // Find the formfield method in ForeignKey class around line 771
  val startLine = 770
  val endLine = 900
  lines.slice(startLine, endLine).zipWithIndex.foreach { case (line, idx) =>
    println(s"${startLine + idx + 1}: $line")
  }
}