requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo") { 
  // Find files related to ForeignKey
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo/django/db/models", "class ForeignKey", "*.py")
  matches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line.trim}"))
}