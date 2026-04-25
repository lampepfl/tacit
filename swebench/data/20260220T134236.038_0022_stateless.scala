requestExecPermission(Set("python3")) {
  val result = exec("python3", List("-m", "py_compile", "django/contrib/auth/forms.py"), workingDir = Some("/Users/tacit/Work/SafeExec/swebench_rMCP/benchuns/20260220_122941/workspace/django__django-14411/repo"))
  println(s"Exit code: ${result.exitCode}")
  println(s"Stdout: ${result.stdout}")
  println(s"Stderr: ${result.stderr}")
}
