requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo") {
  val wcsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo/astropy/wcs/wcs.py")
  val lines = wcsFile.readLines()
  // Get lines 1208-1280 to see the _array_converter function
  lines.slice(1207, 1290).zipWithIndex.map { case (line, idx) => s"${idx + 1208}: $line" }
}