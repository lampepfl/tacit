requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15738/repo") { (fs: FileSystem) =>
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15738/repo")
  println(s"Root path: ${root.path}")
  println(s"Is directory: ${root.isDirectory}")
  
  // List children
  val children = root.children
  println(s"Number of children: ${children.length}")
  children.foreach(c => println(s"  ${c.name} (dir=${c.isDirectory})"))
}