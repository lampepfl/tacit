requestExecPermission(Set("python", "python3")) {
  val result = exec("python", List("tests/runtests.py", "admin_inlines.tests.TestVerboseNameInlineForms", "-v", "2"), 
    workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13710/repo"),
    timeoutMs = 120000)
  println(s"Exit code: ${result.exitCode}")
  println(s"Output: ${result.stdout}")
  println(s"Errors: ${result.stderr}")
}