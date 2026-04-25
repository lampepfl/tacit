requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11905/repo") { 
  val hits = grep("django/db/models/sql/query.py", "def resolve_lookup_value")
  hits.foreach(m => println(s"${m.file}:${m.lineNumber}: ${m.line}"))
}