requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  requestExecPermission(Set("pip", "python")) {
    val result = exec("pip", List("install", "-e", ".", "-q"), workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo"), timeoutMs = 300000)
    println(s"Exit code: ${result.exitCode}")
    println(s"Stdout: ${result.stdout}")
    if (result.stderr.nonEmpty) {
      println(s"Stderr: ${result.stderr}")
    }
  }
}