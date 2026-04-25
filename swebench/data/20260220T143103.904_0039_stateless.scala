requestExecPermission(Set("cat", "python3")) {
  // Use cat to create the test file
  val script = """import sys
sys.path.insert(0, '/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo')

import django
from django.conf import settings
settings.configure(
    DEBUG=True,
    DATABASES={'default': {'ENGINE': 'django.db.backends.sqlite3', 'NAME': ':memory:'}},
    INSTALLED_APPS=['django.contrib.contenttypes', 'django.contrib.auth'],
)
django.setup()

from django.db.migrations.serializer import TypeSerializer, serializer_factory

class Outer:
    class Inner:
        pass

serializer = TypeSerializer(Outer.Inner)
result, imports = serializer.serialize()
print(f"Serialized: {result}")
print(f"Imports: {imports}")

if "Outer.Inner" in result:
    print("SUCCESS: Inner class path correctly includes Outer.Inner")
else:
    print("FAILURE: Inner class path does not include Outer.Inner")
"""
  // Write using exec echo
  val result = exec("cat", List("- > /Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo/test_inner_class.py"), None)
  println(s"Exit: ${result.exitCode}")
}