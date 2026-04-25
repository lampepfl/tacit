requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo") { 
  requestExecPermission(Set("git")) {
    val result = exec("git", List("diff", "astropy/nddata/mixins/ndarithmetic.py"), workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14995/repo"))
    println(s"Exit code: ${result.exitCode}")
    println(s"Stdout: ${result.stdout}")
    println(s"Stderr: ${result.stderr}")
  }
}