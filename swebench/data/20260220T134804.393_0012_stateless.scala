requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14787/repo") {
  requestExecPermission(Set("python")) {
    val result = exec("python", List("-c", """
import os
import sys
import django
from functools import wraps

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'tests.settings')
sys.path.insert(0, '/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14787/repo')

django.setup()

from django.utils.decorators import method_decorator

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

try:
    t = Test()
    result = t.hello_world()
    print(f"SUCCESS: Result = {result}")
except AttributeError as e:
    print(f"FAILED: {e}")
"""), Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14787/repo"), 30000)
    println(s"Exit code: ${result.exitCode}")
    println(s"Stdout: ${result.stdout}")
    println(s"Stderr: ${result.stderr}")
  }
}