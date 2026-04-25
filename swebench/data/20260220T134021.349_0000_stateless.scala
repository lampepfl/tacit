requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14411/repo") { 
  val matches = grepRecursive(".", "ReadOnlyPasswordHashWidget")
  matches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}
