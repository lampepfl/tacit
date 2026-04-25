requestExecPermission(Set("python3")) {
  exec("python3", List("-c", """
import re
content = open('/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/tests/urlpatterns_reverse/tests.py').read()
# Find any tests related to partial and repr
matches = re.findall(r'def test.*partial.*', content, re.IGNORECASE)
print('Found tests:', matches)
"""))
}