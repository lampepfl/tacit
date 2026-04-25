requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py")
  val content = f.read()
  val lines = content.split("\n").toVector
  
  // Insert new lines after line 521 (index 520)
  // The new lines should be:
  //         elif self.mask is not None and operand is not None and operand.mask is None:
  //             # Make a copy so there is no reference in the result.
  //             return deepcopy(self.mask)
  
  val newLines = lines.slice(0, 521) ++ 
    List(
      "        elif self.mask is not None and operand is not None and operand.mask is None:",
      "            # Make a copy so there is no reference in the result.",
      "            return deepcopy(self.mask)"
    ) ++ 
    lines.slice(521, lines.length)
  
  val newContent = newLines.mkString("\n")
  f.write(newContent)
  println("File updated successfully")
}
