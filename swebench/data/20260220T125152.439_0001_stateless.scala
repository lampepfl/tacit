requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo") { (fs: FileSystem) =>
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12908/repo")
  root.children.map(f => s"${f.name} (dir=${f.isDirectory})").mkString("\n")
}