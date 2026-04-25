requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo") {
  val fs = summon[FileSystem]
  val base = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo/django/core/management/base.py")
  val content = base.read()
  // Search for help-related text processing
  val lines = content.split("\n").zipWithIndex
  lines.foreach { case (line, idx) =>
    if (line.toLowerCase.contains("help") && (line.contains("format") || line.contains("text") || line.contains("split") || line.contains("line"))) {
      println(s"${idx+1}: $line")
    }
  }
}