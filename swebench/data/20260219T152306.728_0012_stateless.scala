requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260219_162036/workspace/astropy__astropy-12907/repo") {
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260219_162036/workspace/astropy__astropy-12907/repo/astropy/modeling/separable.py")
  val content = file.read()
  val lines = content.split("\n")
  
  // Fix line 245: change "= 1" to "= right"
  lines(244) = "        cright[-right.shape[0]:, -right.shape[1]:] = right"
  
  val newContent = lines.mkString("\n")
  file.write(newContent)
  println("File updated successfully")
}