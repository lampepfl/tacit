def searchCode(using fs: FileSystem): String = 
  val matches = grepRecursive("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15790/repo/django/template", "check_for_template_tags_with_the_same_name", "*.py")
  if matches.isEmpty then "No matches found"
  else matches.map(m => s"${m.file}:${m.lineNumber}: ${m.line}").mkString("\n")

val result = requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15790/repo")(searchCode)
result