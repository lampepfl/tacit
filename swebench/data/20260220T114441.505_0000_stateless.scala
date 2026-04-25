requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val root = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo")
  println(s"Root: ${root.path}")
  println(s"Is directory: ${root.isDirectory}")
  root.children.take(20).foreach(c => println(s"  ${c.name}"))
}
