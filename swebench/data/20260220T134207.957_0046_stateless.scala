requestExecPermission(Set("python3")) {
  exec("python3", List("-c", "import os; print(os.listdir('/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/django/urls'))"))
}