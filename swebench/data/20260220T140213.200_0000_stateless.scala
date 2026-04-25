requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15498/repo") {
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15498/repo")
  println(s"Root: ${root.path}")
  println(s"Exists: ${root.exists}")
  root.children.take(10).foreach(c => println(s"  ${c.name}"))
}