requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py")
  val lines = f.readLines()
  
  // Find the _arithmetic_mask method
  lines.zipWithIndex.filter { case (line, idx) => 
    idx >= 514 && idx <= 540
  }.foreach { case (line, idx) =>
    println(s"${idx+1}: $line")
  }
}