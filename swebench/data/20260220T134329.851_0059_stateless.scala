requestExecPermission(Set("python3")) {
  exec("python3", List("-c", """
import re

# Read the file
with open('/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/django/urls/resolvers.py', 'r') as f:
    content = f.read()

# Find the __init__ method and add the partial unwrapping
# The current code is:
#     if not hasattr(func, '__name__'):
#         # A class-based view
#         self._func_path = func.__class__.__module__ + '.' + func.__class__.__name__
#     else:
#         # A function-based view
#         self._func_path = func.__module__ + '.' + func.__name__

# We need to add code to unwrap partial before setting _func_path
old_code = '''        if not hasattr(func, '__name__'):
            # A class-based view
            self._func_path = func.__class__.__module__ + '.' + func.__class__.__name__
        else:
            # A function-based view
            self._func_path = func.__module__ + '.' + func.__name__'''

new_code = '''        # Unwrap functools.partial objects.
        while isinstance(func, functools.partial):
            func = func.func

        if not hasattr(func, '__name__'):
            # A class-based view
            self._func_path = func.__class__.__module__ + '.' + func.__class__.__name__
        else:
            # A function-based view
            self._func_path = func.__module__ + '.' + func.__name__'''

if old_code in content:
    content = content.replace(old_code, new_code)
    with open('/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo/django/urls/resolvers.py', 'w') as f:
        f.write(content)
    print('File updated successfully')
else:
    print('Could not find the target code')
    # Let's print around the _func_path setting to debug
    lines = content.split('\\n')
    for i, line in enumerate(lines):
        if '_func_path' in line:
            print(f'Line {i+1}: {line}')
"""))
}