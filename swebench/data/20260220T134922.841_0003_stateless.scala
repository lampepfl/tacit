requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14915/repo") { (fs: FileSystem) =>
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14915/repo")
  root.children.take(20).foreach(c => println(c.name))
}