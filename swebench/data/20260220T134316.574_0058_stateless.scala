requestExecPermission(Set("python3")) {
  exec("python3", List("-c", """
import re
content = open('/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/tests/urlpatterns_reverse/views.py').read()
print(content[:3000])
"""))
}