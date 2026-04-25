requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14672/repo") { 
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14672/repo")
  println(s"Root: ${root.name}")
  val children = root.children.map(_.name)
  println(s"Children: ${children.take(20)}")
}
