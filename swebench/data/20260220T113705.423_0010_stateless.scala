requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py")
  val content = f.read()
  
  // Find the exact position
  val idx = content.indexOf("self.mask is None and operand is not None and operand.mask is None")
  println(s"Found at: $idx")
  println(content.slice(idx, idx+200))
}
