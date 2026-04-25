
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15202/repo") {
  requestExecPermission(Set("python")) {
    exec("python", List("-c", """
import sys
sys.path.insert(0, '/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15202/repo')

from django.conf import settings
settings.configure(USE_I18N=False, USE_L10N=False)

from django.core.validators import URLValidator
from django.core.exceptions import ValidationError

validator = URLValidator()

# Test with valid URLs
test_urls = [
    'http://example.com',
    'https://example.com/path',
    'http://localhost:8000/',
    'ftp://ftp.example.com',
]

for url in test_urls:
    try:
        result = validator(url)
        print(f'Valid URL: {url} - OK')
    except ValidationError as e:
        print(f'Valid URL: {url} - FAILED (unexpected): {e}')
    except Exception as e:
        print(f'Valid URL: {url} - ERROR: {type(e).__name__}: {e}')

# Test with invalid URLs  
invalid_urls = [
    '////]@N.AN',
    'not a url',
    'http://',
]

print()
for url in invalid_urls:
    try:
        result = validator(url)
        print(f'Invalid URL: {url} - PASSED (unexpected)')
    except ValidationError as e:
        print(f'Invalid URL: {url} - ValidationError (correct)')
    except ValueError as e:
        print(f'Invalid URL: {url} - ValueError (BUG)')
"""))
  }
}
