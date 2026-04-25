
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15202/repo") {
  requestExecPermission(Set("python")) {
    exec("python", List("-c", """
import os
import django
os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'tests.test_sqlite')
django.setup()

from django import forms
from django.core.exceptions import ValidationError

try:
    result = forms.URLField().clean('////]@N.AN')
    print(f'Result: {result}')
except ValidationError as e:
    print(f'ValidationError: {e.message}')
except ValueError as e:
    print(f'ValueError (BUG!): {e}')
except Exception as e:
    print(f'Other error: {type(e).__name__}: {e}')
"""))
  }
}
