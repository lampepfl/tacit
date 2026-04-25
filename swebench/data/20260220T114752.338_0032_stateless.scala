requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo") {
  val wcsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo/astropy/wcs/wcs.py")
  val content = wcsFile.read()
  // Ensure the file ends with a newline
  if (!content.endsWith("\n")) {
    wcsFile.write(content + "\n")
    "Added newline"
  } else {
    "Already has newline"
  }
}