requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13033/repo") {
  val q = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13033/repo/django/db/models/sql/query.py")
  val content = q.read()
  println(s"File size: ${content.length}")
  println("First 500 chars:")
  println(content.take(500))
}