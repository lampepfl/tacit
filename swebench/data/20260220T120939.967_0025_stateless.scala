requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11905/repo") { 
  val hits = grepRecursive("django/db/models", "build_filter", "*.py")
  hits.take(10).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}