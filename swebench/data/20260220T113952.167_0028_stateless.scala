requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py")
  val content = f.read()
  
  // Find the line and examine exact bytes
  val idx = content.indexOf("elif self.mask is None and operand is not None:")
  // Get 50 chars before and after
  val start = idx - 50
  val end = idx + 180
  println("Before:")
  println(content.slice(start, idx).map(c => if (c == '\t') "<TAB>" else if (c == ' ') "." else c.toString).mkString)
  println("Match:")
  println(content.slice(idx, idx+60).map(c => if (c == '\t') "<TAB>" else if (c == ' ') "." else c.toString).mkString)
}
