requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py")
  val content = f.read()
  val lines = content.split("\n").toVector
  
  // We need to add back the else block and the handle_mask call
  // Lines should be:
  // 525:         elif operand is None:
  // 526:             return deepcopy(self.mask)
  // 527:         else:
  // 528:             # Now lets calculate the resulting mask (operation enforces copy)
  // 529:             return handle_mask(self.mask, operand.mask, **kwds)
  
  val newLines = lines.updated(526, "        else:")
    .updated(527, "            # Now lets calculate the resulting mask (operation enforces copy)")
    .updated(528, "            return handle_mask(self.mask, operand.mask, **kwds)")
  
  val newContent = newLines.mkString("\n")
  f.write(newContent)
  println("File updated successfully")
}
