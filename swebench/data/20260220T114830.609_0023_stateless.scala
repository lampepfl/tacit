requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  requestExecPermission(Set("python")) {
    // Try build_ext --inplace with more output
    val result = exec("python", List("setup.py", "build_ext", "--inplace", "2>&1"), workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo"), timeoutMs = 600000)
    println(s"Exit code: ${result.exitCode}")
    println(s"Stderr: ${result.stderr}")
  }
}