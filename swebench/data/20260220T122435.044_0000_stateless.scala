requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo") {
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo")
  println(s"Root path: ${root.path}")
  val children = root.children
  children.foreach(c => println(s"  ${c.name}"))
}