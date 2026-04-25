requestExecPermission(Set("bash")) {
  val result = exec("bash", List("-c", "cd /Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13710/repo && /opt/homebrew/bin/python3.13 tests/runtests.py admin_inlines.tests.TestVerboseNameInlineForms -v 2"),
    timeoutMs = 180000)
  println(s"Exit code: ${result.exitCode}")
  println(s"Output: ${result.stdout}")
  if (result.stderr.nonEmpty) {
    println(s"Errors: ${result.stderr}")
  }
}