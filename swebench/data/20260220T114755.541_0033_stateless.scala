requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-7746/repo") {
requestExecPermission(Set("git")) {
  exec("git", List("diff", "astropy/wcs/wcs.py"), None, 30000)
}
}