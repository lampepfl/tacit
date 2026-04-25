def searchCode(using fs: FileSystem) = 
  val matches = grep("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15790/repo/django/template/base.py", "check_for_template_tags_with_the_same_name")
  matches.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15790/repo")(searchCode)