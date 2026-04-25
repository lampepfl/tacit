requestExecPermission(Set("python3")) {
  exec("python3", List("-c", """
content = open('/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/django/urls/resolvers.py').read()
# Find line with _func_path
lines = content.split('\\n')
for i, line in enumerate(lines):
    if 'while isinstance' in line:
        print(f'Found at line {i+1}: {line}')
        for j in range(max(0, i-2), min(len(lines), i+8)):
            print(f'{j+1}: {lines[j]}')
        break
else:
    print('Not found')
"""))
}