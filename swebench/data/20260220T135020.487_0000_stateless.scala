requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo") { fs =>
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14999/repo")
  println(s"Root: ${root.name}")
  val children = root.children.map(_.name).take(20)
  println(s"Children: ${children.mkString(", ")}")
}