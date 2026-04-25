requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo") {
  // Let me restore the file using git - need to fix the parameters
  requestExecPermission(Set("git")) {
    exec("git", List("checkout", "astropy/wcs/wcs.py"), None, 30000)
  }
}