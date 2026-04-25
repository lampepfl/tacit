requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo") { 
  val wcsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo/astropy/wcs/wcs.py")
  val lines = wcsFile.readLines()
  // Get full _return_list_of_arrays function (lines 1214-1231)
  lines.slice(1213, 1232).zipWithIndex.map { case (line, idx) => s"${idx + 1214}: $line" }
}