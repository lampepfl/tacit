requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo")
  println(s"Root exists: ${root.exists}")
  println(s"Is directory: ${root.isDirectory}")
  root.children.take(20).foreach(c => println(s"  ${c.name}"))
}
