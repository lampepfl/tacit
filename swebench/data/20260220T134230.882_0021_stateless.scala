requestExecPermission(Set("python3")) {
  val result = exec("python3", List("-c", "import django.contrib.auth.forms"), workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14411/repo"))
  println(s"Exit code: ${result.exitCode}")
  println(s"Stdout: ${result.stdout}")
  println(s"Stderr: ${result.stderr}")
}
