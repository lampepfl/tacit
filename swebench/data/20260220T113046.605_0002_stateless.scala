requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val astropy = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy")
  astropy.children.take(30).foreach(f => println(s"${f.name} (dir=${f.isDirectory})"))
}