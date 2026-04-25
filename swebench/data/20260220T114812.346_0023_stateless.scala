requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-10924/repo") {
  requestExecPermission(Set("python")) {
    exec("python", List("-c", "import django.db.models.fields; print('Import successful')"), None, 10000L)
  }
}