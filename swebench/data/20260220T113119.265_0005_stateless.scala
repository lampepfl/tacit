
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/astropy__astropy-14365/repo") {
  given fs: FileSystem = summon
  requestExecPermission(Set("python")) {
    val result = exec("python", List("test_fix.py"))
    println(s"Exit code: ${result.exitCode}")
    println(s"stdout: ${result.stdout}")
    println(s"stderr: ${result.stderr}")
  }
}
