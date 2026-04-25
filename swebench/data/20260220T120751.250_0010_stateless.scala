requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11905/repo") { 
  val hits = grepRecursive("django", "ValueError", "*.py")
  val filtered = hits.filter(m => m.line.contains("bool") || m.line.contains("boolean") || m.line.contains("must be"))
  filtered.take(20).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}