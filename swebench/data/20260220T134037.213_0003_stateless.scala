requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14411/repo") { 
  val files = find("django/contrib/admin", "*.py")
  files.filter(_.contains("widget") || _.contains("password")).foreach(println)
}
