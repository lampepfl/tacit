requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15347/repo") {
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15347/repo/django/contrib/messages", "if.*extra_tags:")
  matches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}