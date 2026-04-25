requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo") { (fs: FileSystem) =>
  given FileSystem = fs
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo")
  println(root.children.map(_.name).take(10).mkString(", "))
}