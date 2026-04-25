requestExecPermission(Set("bash", "pip")) {
  // Try to install backports.cgi-parser for Python 3.14
  val result = exec("bash", List("-c", "/Users/tacit/Work/SafeExecMCP/bench/.venv/bin/pip install cgi-fix"),
    timeoutMs = 60000)
  println(s"Exit code: ${result.exitCode}")
  println(s"Output: ${result.stdout}")
  if (result.stderr.nonEmpty) {
    println(s"Errors: ${result.stderr}")
  }
}