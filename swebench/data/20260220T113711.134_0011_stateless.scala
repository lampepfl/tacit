requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py")
  val content = f.read()
  val lines = content.split("\n")
  
  // Find the exact lines we need to modify
  val startLine = 514  // "# If only one mask is present"
  val endLine = 526    // "return handle_mask(self.mask, operand.mask, **kwds)"
  
  println("Original lines:")
  for i <- startLine-1 to endLine-1 do
    println(s"${i+1}: ${lines(i)}")
}
