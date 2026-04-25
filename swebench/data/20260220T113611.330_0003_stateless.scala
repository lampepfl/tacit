requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-12907/repo") { 
  val f = access("astropy/modeling/separable.py")
  val lines = f.readLines()
  // Find the _cstack function around line 200
  lines.slice(190, 220).foreach(println)
}