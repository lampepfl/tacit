requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13658/repo/django") { fs =>
  val baseDir = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13658/repo/django/core")
  println(s"Directory exists: ${baseDir.exists}, isDirectory: ${baseDir.isDirectory}")
  if baseDir.isDirectory then
    baseDir.children.foreach(c => println(s"  ${c.name}"))
}
