requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo")(fs => 
  val repo = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo")
  repo.children.take(20).foreach(f => println(s"${f.name} (dir=${f.isDirectory})"))
)