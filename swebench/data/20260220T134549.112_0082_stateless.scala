requestExecPermission(Set("python3")) {
  exec("python3", List("-c", """
content = open('/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/django/urls/resolvers.py').read()

# Look for self.func being used
lines = content.split('\\n')
for i, line in enumerate(lines):
    if 'self.func' in line and '_func_path' not in line and 'self.func =' not in line:
        print(f'Line {i+1}: {line.strip()}')
"""))
}