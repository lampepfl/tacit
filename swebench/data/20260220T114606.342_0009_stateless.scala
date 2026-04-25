requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py")
  val content = f.read()
  
  // Check if the new code exists
  if (content.contains("elif self.mask is not None and operand.mask is None:")) {
    println("Fix already applied!")
  } else {
    println("Fix NOT applied, need to try again")
    // Let's find the exact text
    val idx = content.indexOf("elif operand is None:")
    println(s"Found 'elif operand is None:' at index $idx")
    println("Content around that area:")
    println(content.substring(idx - 100, idx + 200))
  }
}