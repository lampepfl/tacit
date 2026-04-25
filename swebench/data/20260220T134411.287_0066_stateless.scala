requestExecPermission(Set("python3")) {
  exec("python3", List("-c", """
import re
content = open('/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/django/urls/resolvers.py').read()
# Check if self.func is used elsewhere in the class
matches = re.findall(r'self\.func[^_]', content)
print('self.func references:', len(matches))
for m in matches[:20]:
    print(m)
"""))
}