
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11999/repo") {
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11999/repo/django/db/models")
  root.children.take(30).foreach(f => println(s"${f.name} (dir=${f.isDirectory})"))
}
