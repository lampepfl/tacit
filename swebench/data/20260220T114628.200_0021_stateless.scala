requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo") { 
  requestExecPermission(Set("pip", "python")) {
    exec("pip", List("install", "-e", ".", "--no-build-isolation"), Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo"), 180000)
  }
}
