requestExecPermission(Set("python3")) {
  exec("python3", List("-c", """
import subprocess
result = subprocess.run(['grep', '-r', 'ResolverMatch', '/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/tests/'], capture_output=True, text=True)
print('Found', result.stdout.count('\\n'), 'lines')
print(result.stdout[:3000])
"""))
}