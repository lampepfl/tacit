requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14787/repo") {
  requestExecPermission(Set("python")) {
    val testScript = """
import os
import sys
import django
from functools import wraps

# Setup Django settings
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'tests.settings')
sys.path.insert(0, '/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14787/repo')

django.setup()

from django.utils.decorators import method_decorator

# Test case from the bug report
def logger(func):
    @wraps(func)
    def inner(*args, **kwargs):
        try:
            result = func(*args, **kwargs)
        except Exception as e:
            result = str(e)
        finally:
            print(f"{func.__name__} called with args: {args}")
        return result
    return inner

class Test:
    @method_decorator(logger)
    def hello_world(self):
        return "hello"

# Test it
try:
    t = Test()
    result = t.hello_world()
    print(f"SUCCESS: Result = {result}")
except AttributeError as e:
    print(f"FAILED: {e}")
"""
    
    val testFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14787/repo/test_fix.py")
    testFile.write(testScript)
  }
}