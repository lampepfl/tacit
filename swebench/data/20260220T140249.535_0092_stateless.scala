
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14997/repo") {
  requestExecPermission(Set("python")) {
    val result = exec("python", List("-c", "import sys; sys.path.insert(0, '/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14997/repo'); from django.db.backends.ddl_references import Expressions; print('OK')"))
    println(result.stdout)
    println(result.stderr)
  }
}
