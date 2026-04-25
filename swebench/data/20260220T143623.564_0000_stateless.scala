requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15790/repo") {
  val matches = grepRecursive(".", "check_for_template_tags_with_the_same_name", "*.py")
  matches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}