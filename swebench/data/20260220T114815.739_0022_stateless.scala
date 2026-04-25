requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  requestExecPermission(Set("python")) {
    // Try build_ext --inplace
    val result = exec("python", List("setup.py", "build_ext", "--inplace"), workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo"), timeoutMs = 600000)
    println(s"Exit code: ${result.exitCode}")
    if (result.stdout.length > 1000) {
      println(s"Stdout (first 1000): ${result.stdout.take(1000)}")
    } else {
      println(s"Stdout: ${result.stdout}")
    }
    if (result.stderr.length > 500) {
      println(s"Stderr (first 500): ${result.stderr.take(500)}")
    } else {
      println(s"Stderr: ${result.stderr}")
    }
  }
}