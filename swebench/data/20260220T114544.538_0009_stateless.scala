requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo") { 
  val coreFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo/astropy/io/ascii/core.py")
  val lines = coreFile.readLines()
  val startLine = lines.indexWhere(_.contains("def write(self, lines):"))
  if (startLine >= 0) {
    val relevantLines = lines.slice(startLine, startLine + 30)
    relevantLines.foreach(println)
  }
}
