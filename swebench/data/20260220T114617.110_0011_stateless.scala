requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py")
  val content = f.read()
  
  // Find the exact position 
  val start = content.indexOf("elif operand is None:")
  val before = content.lastIndexOf("elif self.mask is None", start)
  println("Text between elif conditions:")
  println(content.substring(before, start + 80))
}