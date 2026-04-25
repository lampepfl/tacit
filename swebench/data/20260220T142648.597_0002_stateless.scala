requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo") { (fs: FileSystem) =>
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo")
  println(s"Root path: ${root.path}")
  root.children.take(20).foreach(c => println(s"  ${c.name}"))
}