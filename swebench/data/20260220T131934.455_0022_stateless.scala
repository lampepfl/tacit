
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13447/repo") {
  requestExecPermission(Set("python3")) {
    val result = exec("python3", List("-c", "import django; from django.contrib.admin import sites; print('Import OK')"))
    println(s"Exit code: ${result.exitCode}")
    println(s"Output: ${result.stdout}")
    if (result.stderr.nonEmpty) {
      println(s"Stderr: ${result.stderr}")
    }
  }
}
