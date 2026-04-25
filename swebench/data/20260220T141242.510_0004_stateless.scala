requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15790/repo") { fs =>
  val dir = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15790/repo")
  dir.children.take(20).foreach(e => println(s"${e.name} (dir=${e.isDirectory})"))
}