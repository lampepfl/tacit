requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-12907/repo") { 
  val f = access("astropy/modeling/separable.py")
  val content = f.read()
  // Just fix the bug on line 245: change "= 1" to "= right"
  val newContent = content.replace("cright[-right.shape[0]:, -right.shape[1]:] = 1", "cright[-right.shape[0]:, -right.shape[1]:] = right")
  access("astropy/modeling/separable.py").write(newContent)
  println("Done!")
}