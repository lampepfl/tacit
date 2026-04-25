requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15061/repo") {
  // Search for MultiWidget in the django directory
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15061/repo/django", "class MultiWidget", "*.py")
  matches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}