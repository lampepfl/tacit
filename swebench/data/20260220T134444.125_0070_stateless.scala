requestExecPermission(Set("python3")) {
  exec("python3", List("-c", """
import re
content = open('/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/django/urls/resolvers.py').read()
# Find the partial unwrapping code
if 'while isinstance(func, functools.partial):' in content:
    print('Change found: partial unwrapping is present')
    # Show the context
    lines = content.split('\\n')
    for i, line in enumerate(lines):
        if 'while isinstance(func, functools.partial):' in line:
            print('\\nContext (lines', i, '-', i+10, '):')
            for j in range(max(0, i-2), min(len(lines), i+12)):
                print(f'{j+1}: {lines[j]}')
else:
    print('Change not found!')
"""))
}