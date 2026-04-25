requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo/astropy/nddata/mixins/ndarithmetic.py")
  var content = f.read()
  
  // Let's be more precise - find the exact pattern
  val target = "        elif operand is None:\n            return deepcopy(self.mask)\n        else:"
  val replacement = "        elif operand is None:\n            return deepcopy(self.mask)\n        elif self.mask is not None and operand.mask is None:\n            # Make a copy so there is no reference in the result.\n            return deepcopy(self.mask)\n        else:"
  
  if (content.contains(target)) {
    println("Found target pattern")
    content = content.replace(target, replacement)
    f.write(content)
    println("Written!")
  } else {
    println("Target not found")
    // Debug
    val idx = content.indexOf("elif operand is None:")
    val snippet = content.substring(idx-50, idx+100)
    println("Snippet: ")
    println(snippet)
    println("---")
    println("Chars around 'elif operand is None':")
    for (i <- idx - 20 to idx + 50) {
      print(content(i))
    }
    println()
  }
}