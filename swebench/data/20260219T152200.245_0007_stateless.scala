requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260219_162036/workspace/astropy__astropy-12907/repo") {
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260219_162036/workspace/astropy__astropy-12907/repo/astropy/modeling/separable.py")
  val lines = file.readLines()
  lines.zipWithIndex.drop(150).take(100).foreach { case (line, idx) => 
    println(s"${idx + 1}: $line") 
  }
}