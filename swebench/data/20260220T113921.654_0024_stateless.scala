requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py")
  val content = f.read()
  
  // Find the exact string to replace (using the exact whitespace)
  val searchStr = """        elif self.mask is None and operand is not None:
            # Make a copy so there is no reference in the result.
            return deepcopy(operand.mask)
        elif operand is None:"""
  
  val replaceStr = """        elif self.mask is None and operand is not None:
            # Make a copy so there is no reference in the result.
            return deepcopy(operand.mask)
        elif self.mask is not None and operand is not None and operand.mask is None:
            # Make a copy so there is no reference in the result.
            return deepcopy(self.mask)
        elif operand is None:"""
  
  val newContent = content.replaceFirst(searchStr, replaceStr)
  
  if (newContent == content) then
    println("WARNING: No replacement made!")
    // Let's try to find the pattern
    val idx = content.indexOf("elif self.mask is None and operand is not None:")
    println(s"Found at: $idx")
  else
    f.write(newContent)
    println("File updated successfully")
}
