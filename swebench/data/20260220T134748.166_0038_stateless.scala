requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  requestExecPermission(Set("python3", "python")) {
    val testCode = """
import django
from django.conf import settings

# Configure Django settings
settings.configure(
    DEBUG=True,
    DATABASES={
        'default': {
            'ENGINE': 'django.db.backends.sqlite3',
            'NAME': ':memory:',
        }
    },
    INSTALLED_APPS=[
        'django.contrib.contenttypes',
        'django.contrib.auth',
    ],
    DEFAULT_AUTO_FIELD='django.db.models.BigAutoField',
)

django.setup()

from django.db import models
from django.core import checks

# Test 1: Self-referential M2M with related_name (should error)
print("Test 1: Self-referential M2M with related_name")
class TestModel1(models.Model):
    friends = models.ManyToManyField('self', related_name='friendships')
    class Meta:
        app_label = 'test'

errors = TestModel1.check()
for e in errors:
    if 'fields.E332' in str(e.id):
        print(f"  PASS: Found expected error E332: {e.msg}")

# Test 2: Self-referential M2M without related_name (should not error)
print("\nTest 2: Self-referential M2M without related_name")
class TestModel2(models.Model):
    friends = models.ManyToManyField('self')
    class Meta:
        app_label = 'test'

errors = TestModel2.check()
e332_errors = [e for e in errors if 'fields.E332' in str(getattr(e, 'id', ''))]
if not e332_errors:
    print("  PASS: No E332 error as expected")

# Test 3: M2M to another model with related_name (should not error)
print("\nTest 3: M2M to another model with related_name")
class TestModel3A(models.Model):
    name = models.CharField(max_length=100)
    class Meta:
        app_label = 'test'

class TestModel3B(models.Model):
    items = models.ManyToManyField(TestModel3A, related_name='owners')
    class Meta:
        app_label = 'test'

errors = TestModel3B.check()
e332_errors = [e for e in errors if 'fields.E332' in str(getattr(e, 'id', ''))]
if not e332_errors:
    print("  PASS: No E332 error as expected")

# Test 4: Explicit symmetrical=True with related_name (should error)
print("\nTest 4: Explicit symmetrical=True with related_name")
class TestModel4(models.Model):
    tags = models.ManyToManyField('self', symmetrical=True, related_name='tagged')
    class Meta:
        app_label = 'test'

errors = TestModel4.check()
e332_errors = [e for e in errors if 'fields.E332' in str(getattr(e, 'id', ''))]
if e332_errors:
    print(f"  PASS: Found expected error E332")
else:
    print(f"  FAIL: Expected error E332 but got: {errors}")

# Test 5: Explicit symmetrical=False with related_name (should not error)
print("\nTest 5: Explicit symmetrical=False with related_name")
class TestModel5(models.Model):
    tags = models.ManyToManyField('self', symmetrical=False, related_name='tagged')
    class Meta:
        app_label = 'test'

errors = TestModel5.check()
e332_errors = [e for e in errors if 'fields.E332' in str(getattr(e, 'id', ''))]
if not e332_errors:
    print("  PASS: No E332 error as expected")

print("\nAll tests completed!")
"""
    
    val result = exec("python3", List("-c", testCode), workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo"))
    println(s"Exit code: ${result.exitCode}")
    println(s"stdout: ${result.stdout}")
    println(s"stderr: ${result.stderr}")
  }
}
