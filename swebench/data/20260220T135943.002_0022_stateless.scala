requestExecPermission(Set("python3")) {
  val result = exec("python3", List("-c", "import django; print(django.VERSION)"))
  println(s"Exit code: ${result.exitCode}")
  println(s"Stdout: ${result.stdout}")
}