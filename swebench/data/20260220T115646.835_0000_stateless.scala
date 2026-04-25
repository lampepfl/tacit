
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11564/repo") {
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11564/repo")
  root.children.take(20).foreach(e => println(s"${e.name} (dir=${e.isDirectory})"))
}
