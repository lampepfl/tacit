requestExecPermission(Set("python3")) {
  exec("python3", List("-c", """
import subprocess
result = subprocess.run(['grep', '-r', 'resolver_match\\.func', '/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/django/', '--include=*.py'], capture_output=True, text=True)
print('Found resolver_match.func references:')
print(result.stdout[:2000])
"""))
}