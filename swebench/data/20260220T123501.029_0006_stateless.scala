
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12589/repo") { (fs: FileSystem) =>
  val entry = fs.access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12589/repo")
  entry.children.map(c => s"${c.name}").take(30).mkString("\n")
}
