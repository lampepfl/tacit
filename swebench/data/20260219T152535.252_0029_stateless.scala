requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260219_162036/workspace/astropy__astropy-12907/repo") {
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260219_162036/workspace/astropy__astropy-12907/repo/astropy/modeling/separable.py")
  val lines = file.readLines()
  
  // Print the _cstack function (lines 219-247)
  for (idx <- 218 to 247) {
    println(s"${idx + 1}: ${lines(idx)}")
  }
}