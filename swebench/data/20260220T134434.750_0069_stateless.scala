requestExecPermission(Set("python3")) {
  exec("python3", List("-c", """
content = open('/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/django/urls/resolvers.py').read()
print('File length:', len(content))
print('\\n' + content[:2000])
"""))
}