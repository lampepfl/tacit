requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13658/repo") { 
  val baseDir = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13658/repo")
  println(s"Directory exists: ${baseDir.exists}")
  baseDir.children.foreach(c => println(s"  ${c.name}"))
}
