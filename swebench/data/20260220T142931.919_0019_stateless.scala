requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13033/repo") {
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13033/repo/django/db/models", "attname", "*.py")
  println(s"Found ${matches.size} matches")
  matches.take(20).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}