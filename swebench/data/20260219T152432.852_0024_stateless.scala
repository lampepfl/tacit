requestExecPermission(Set("pip", "python", "python3")) {
  exec("pip", List("install", "-e", "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260219_162036/workspace/astropy__astropy-12907/repo", "--no-build-isolation"), timeoutMs = 300000)
}