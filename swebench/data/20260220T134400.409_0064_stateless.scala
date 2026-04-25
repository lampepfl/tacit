requestExecPermission(Set("python3")) {
  exec("python3", List("-c", """
# Let's just verify the code is syntactically correct
import ast
code = open('/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/django/urls/resolvers.py').read()
ast.parse(code)
print('Code is syntactically correct')
"""))
}