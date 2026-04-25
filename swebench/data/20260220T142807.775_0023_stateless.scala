requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo") { 
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo/tests", "get_.*_display.*inherit", "*.py")
  matches.take(10).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}