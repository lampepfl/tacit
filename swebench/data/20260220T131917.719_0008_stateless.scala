requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13590/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13590/repo/django/db/models/sql/query.py")
  val content = f.read()
  val lines = content.split("\n").toList
  lines.zipWithIndex.filter((line, i) => line.contains("resolve_lookup_value")).foreach(println)
}
