requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py")
  val content = f.read()
  
  // Find each line exactly
  val lines = content.split("\n")
  // Find the line with "elif self.mask is None"
  var found = false
  for (i <- 0 until lines.length) do
    if lines(i).contains("elif self.mask is None and operand is not None:") then
      println(s"Found at line $i:")
      println(s"  Prev: [${lines(i-1)}]")
      println(s"  Line: [${lines(i)}]")
      println(s"  Next: [${lines(i+1)}]")
      println(s"  Next2: [${lines(i+2)}]")
      println(s"  Next3: [${lines(i+3)}]")
      found = true
  
  if (!found) then println("Not found!")
}
