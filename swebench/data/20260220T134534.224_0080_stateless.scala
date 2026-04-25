requestExecPermission(Set("python3")) {
  exec("python3", List("-c", """
# Check if there are any existing tests that might break
import os
test_file = '/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/tests/urlpatterns_reverse/tests.py'
content = open(test_file).read()

# Check for tests involving partial
if 'partial' in content.lower():
    print('Found partial in test file')
    # Find lines with partial
    for i, line in enumerate(content.split('\\n')):
        if 'partial' in line.lower() and 'def test' in line.lower():
            print(f'Line {i+1}: {line}')
else:
    print('No partial found')
"""))
}