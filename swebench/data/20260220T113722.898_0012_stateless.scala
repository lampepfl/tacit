requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py")
  val content = f.read()
  val lines = content.split("\n").toVector
  
  // Modify lines 520-524 to add the new condition
  // Original:
  // 520:         elif self.mask is None and operand is not None:
  // 521:             # Make a copy so there is no reference in the result.
  // 522:             return deepcopy(operand.mask)
  // 523:         elif operand is None:
  // 524:             return deepcopy(self.mask)
  
  // New:
  // 520:         elif self.mask is None and operand is not None:
  // 521:             # Make a copy so there is no reference in the result.
  // 522:             return deepcopy(operand.mask)
  // 523:         elif self.mask is not None and operand is not None and operand.mask is None:
  // 524:             # Make a copy so there is no reference in the result.
  // 525:             return deepcopy(self.mask)
  // 526:         elif operand is None:
  // 527:             return deepcopy(self.mask)
  
  val newLines = lines.updated(519, "        elif self.mask is None and operand is not None:")  // line 520
    .updated(520, "            # Make a copy so there is no reference in the result.")  // line 521
    .updated(521, "            return deepcopy(operand.mask)")  // line 522
    .updated(522, "        elif self.mask is not None and operand is not None and operand.mask is None:")  // line 523 - NEW
    .updated(523, "            # Make a copy so there is no reference in the result.")  // line 524 - NEW
    .updated(524, "            return deepcopy(self.mask)")  // line 525 - was 523
    .updated(525, "        elif operand is None:")  // line 526 - was 524
    .updated(526, "            return deepcopy(self.mask)")  // line 527 - was 525
  
  val newContent = newLines.mkString("\n")
  f.write(newContent)
  println("File updated successfully")
}
