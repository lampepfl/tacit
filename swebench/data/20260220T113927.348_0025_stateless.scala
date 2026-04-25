requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py")
  val content = f.read()
  
  // Let's look at what's around the target area
  val idx = content.indexOf("elif self.mask is None and operand is not None:")
  val snippet = content.slice(idx, idx+200)
  println(snippet)
}
