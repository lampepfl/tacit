requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo") { 
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo")
  println(s"Root path: ${root.path}")
  println(s"Children count: ${root.children.length}")
  root.children.take(20).foreach(c => println(s"  ${c.name}"))
}
