
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14997/repo") {
  requestExecPermission(Set("python", "pytest")) {
    exec("python", List("-c", "import django; print(django.__version__)"), None, Some(30000))
  }
}
