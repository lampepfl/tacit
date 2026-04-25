requestExecPermission(Set("python3")) {
  exec("python3", List("-c", """
import ast
content = open('/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/django/urls/resolvers.py').read()
ast.parse(content)
print('Syntax OK')

# Check the import is there
print('functools imported:', 'import functools' in content)
"""))
}