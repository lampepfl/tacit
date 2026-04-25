requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo") { 
  val wcsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo/astropy/wcs/wcs.py")
  val lines = wcsFile.readLines()
  // Get lines around _array_converter (1208 onwards)
  lines.slice(1245, 1290).zipWithIndex.map { case (line, idx) => s"${idx + 1246}: $line" }
}