requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo") { 
  // Search for limit_choices_to in forms
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo/django/forms", "limit_choices_to", "*.py")
  matches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line.trim}"))
}