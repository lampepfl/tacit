requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12983/repo") {
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12983/repo", "def slugify", "*.py")
  matches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}