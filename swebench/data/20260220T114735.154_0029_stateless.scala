requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo") {
  val testFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo/astropy/wcs/tests/test_wcs.py")
  val lines = testFile.readLines()
  lines.slice(0, 60).zipWithIndex.map { case (line, idx) => s"${idx + 1}: $line" }
}