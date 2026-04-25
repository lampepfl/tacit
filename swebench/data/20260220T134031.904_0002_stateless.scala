requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14411/repo") { 
  val root = access(".")
  root.children.foreach(e => println(s"${e.name} (dir=${e.isDirectory})"))
}
