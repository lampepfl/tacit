requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  val checksDir = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo/django/core/checks")
  checksDir.children.foreach(f => println(s"${f.name} (dir=${f.isDirectory})"))
}
