requestExecPermission(Set("python3")) {
  exec("python3", List("-c", """
import subprocess
result = subprocess.run(['head', '-n', '50', '/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/django/urls/resolvers.py'], capture_output=True, text=True)
print(result.stdout)
"""))
}