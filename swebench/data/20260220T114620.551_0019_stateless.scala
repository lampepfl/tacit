requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo") { 
  requestExecPermission(Set("python", "pip")) {
    exec("python", List("-m", "pytest", "astropy/io/ascii/tests/test_rst.py", "-v"), Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14182/repo"))
  }
}
