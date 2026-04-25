requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14411/repo") { 
  val files = find("django/contrib/admin", "*.py")
  val filtered = files.filter(f => f.contains("widget") || f.contains("password"))
  filtered.foreach(println)
}
