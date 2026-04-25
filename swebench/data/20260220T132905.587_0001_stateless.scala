requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo") { 
  val fs = summon[FileSystem]
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo")
  root.children.take(10).foreach(f => println(s"${f.name} (dir=${f.isDirectory})"))
}