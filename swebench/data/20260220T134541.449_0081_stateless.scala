requestExecPermission(Set("python3")) {
  exec("python3", List("-c", """
# Check for any code that accesses partial-specific attributes
content = open('/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/django/urls/resolvers.py').read()

# Look for .func. in the content (accessing the partial's func attribute)
# but not our new code
import re
matches = re.findall(r'.func[^_]', content)
print('References to .func (not _func):', len(matches))
for m in matches[:10]:
    print(m)
"""))
}