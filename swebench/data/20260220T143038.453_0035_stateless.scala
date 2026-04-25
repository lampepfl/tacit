requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo") { 
  val testScript = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12125/repo/test_inner_class.py")
  val scriptContent = """import sys
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

# Test with an inner class
class Outer:
    class Inner:
        pass

# Serialize the Inner class
serializer = TypeSerializer(Outer.Inner)
result, imports = serializer.serialize()
print(f"Serialized: {result}")
print(f"Imports: {imports}")

# Check if it contains the full path
if "Outer.Inner" in result:
    print("SUCCESS: Inner class path correctly includes Outer.Inner")
else:
    print("FAILURE: Inner class path does not include Outer.Inner")
"""
  testScript.write(scriptContent)
  println("Test script created!")
}