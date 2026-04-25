requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12113/repo") {
  requestExecPermission(Set("python")) {
    exec("python", List("-c", "import django; from django.db.backends.sqlite3.base import DatabaseWrapper; print('Import successful')"), Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12113/repo"))
  }
}