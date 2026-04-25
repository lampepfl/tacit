requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo") { 
  val testFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo/astropy/io/ascii/tests/test_fixedwidth.py")
  val lines = testFile.readLines()
  val startLine = lines.indexWhere(_.contains("def test_fixed_width_header_rows"))
  if (startLine >= 0) {
    val endLine = lines.indexWhere(_.startsWith("def "), startLine + 1)
    val relevantLines = lines.slice(startLine, if (endLine > 0) endLine else lines.length)
    relevantLines.foreach(println)
  }
}
