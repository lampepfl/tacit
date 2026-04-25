requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py")
  var content = f.read()
  
  // The old text we want to replace (exactly as it appears)
  val oldText = """        elif operand is None:
            return deepcopy(self.mask)
        else:
            # Now lets calculate the resulting mask (operation enforces copy)
            return handle_mask(self.mask, operand.mask, **kwds)"""

  // The new text
  val newText = """        elif operand is None:
            return deepcopy(self.mask)
        elif self.mask is not None and operand.mask is None:
            # Make a copy so there is no reference in the result.
            return deepcopy(self.mask)
        else:
            # Now lets calculate the resulting mask (operation enforces copy)
            return handle_mask(self.mask, operand.mask, **kwds)"""

  content = content.replace(oldText, newText)
  f.write(content)
  println("File updated!")
}