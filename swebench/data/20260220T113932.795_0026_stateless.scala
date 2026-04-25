requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py")
  val content = f.read()
  
  // Let's see what the exact characters are
  val idx = content.indexOf("elif self.mask is None and operand is not None:")
  val snippet = content.slice(idx-8, idx+150)
  println(snippet)
}
