requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py")
  val content = f.read()
  
  // Check if fix exists
  if (content.contains("elif self.mask is not None and operand.mask is None:")) {
    println("Fix applied!")
  } else {
    println("Still not applied, trying different approach")
    // Find the exact positions
    val p1 = content.indexOf("elif operand is None:")
    println(s"'elif operand is None:' at $p1")
    // Print characters around it
    println("Around that area:")
    println(content.substring(p1-80, p1+120))
  }
}