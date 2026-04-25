requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13220/repo") { 
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13220/repo")
  println(root.name)
  val children = root.children.take(20)
  children.foreach(c => println(s"${c.name} (${if(c.isDirectory) "dir" else "file"})"))
}
