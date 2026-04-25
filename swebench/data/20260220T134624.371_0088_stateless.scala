requestExecPermission(Set("python3")) {
  exec("python3", List("-c", """
import os
# Check for setup.py, pyproject.toml, etc.
for f in ['setup.py', 'pyproject.toml', 'tox.ini', 'Makefile']:
    path = f'/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/{f}'
    if os.path.exists(path):
        print(f'Found: {f}')
        # Show relevant lines
        with open(path) as file:
            for i, line in enumerate(file):
                if 'test' in line.lower() or 'lint' in line.lower() or 'check' in line.lower():
                    print(f'  {line.strip()[:80]}')
"""))
}