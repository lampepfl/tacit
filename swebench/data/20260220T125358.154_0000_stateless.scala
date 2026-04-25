
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13028/repo") {
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13028/repo")
  root.children.take(20).foreach(f => println(s"${f.name} (dir=${f.isDirectory})"))
}
