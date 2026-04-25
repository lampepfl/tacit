
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15320/repo") {
  val matches = grepRecursive(".", "def as_sql", "*.py")
  matches.take(30).foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}
