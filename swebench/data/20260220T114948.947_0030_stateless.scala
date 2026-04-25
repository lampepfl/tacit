requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  requestExecPermission(Set("pip")) {
    // Try installing with --no-build-isolation to use the existing environment
    val result = exec("pip", List("install", "--no-build-isolation", "-e", ".", "-q"), workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo"), timeoutMs = 600000)
    println(s"Exit code: ${result.exitCode}")
    if (result.stdout.nonEmpty) println(s"Stdout: ${result.stdout}")
    if (result.stderr.length < 500) println(s"Stderr: ${result.stderr}")
    else println(s"Stderr (first 500): ${result.stderr.take(500)}")
  }
}