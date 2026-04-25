requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo") {
  val wcsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo/astropy/wcs/wcs.py")
  val content = wcsFile.read()
  // Get the original content around lines 1214-1232
  val lines = content.split("\n")
  lines.slice(1213, 1232).mkString("\n")
}