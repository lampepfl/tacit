requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  // Find Http404
  val grepMatches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo/django", "^class Http404", "*.py")
  grepMatches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}