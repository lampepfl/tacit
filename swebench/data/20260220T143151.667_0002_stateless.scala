requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15738/repo") { fs =>
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15738/repo")
  println("Root path: " + root.path)
  println("Is directory: " + root.isDirectory)
  
  val children = root.children
  println("Number of children: " + children.length)
  children.foreach(c => println("  " + c.name + " (dir=" + c.isDirectory + ")"))
}