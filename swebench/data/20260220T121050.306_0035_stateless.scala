requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11905/repo") { 
  val hits = grep("django/contrib/admin/views/main.py", "lookup_params")
  hits.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}