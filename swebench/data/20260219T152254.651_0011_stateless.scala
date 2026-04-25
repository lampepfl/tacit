requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260219_162036/workspace/astropy__astropy-12907/repo") {
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260219_162036/workspace/astropy__astropy-12907/repo/astropy/modeling/separable.py")
  val content = file.read()
  val lines = content.split("\n")
  println(s"Total lines: ${lines.length}")
  
  // Find the exact lines around _cstack
  lines.zipWithIndex.filter { case (line, idx) => 
    idx >= 235 && idx <= 250
  }.foreach { case (line, idx) =>
    println(s"${idx + 1}: $line")
  }
}