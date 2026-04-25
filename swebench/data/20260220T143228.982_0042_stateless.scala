requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo") { 
  val autoreload = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo/django/utils/autoreload.py")
  val content = autoreload.read()
  val lines = content.split("\n").toList
  // Remove lines 230-232 (0-indexed: 229-231)
  val newLines = lines.patch(230, Nil, 3)
  autoreload.write(newLines.mkString("\n"))
}