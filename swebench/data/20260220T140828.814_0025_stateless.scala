requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo") {
  val fs = summon[FileSystem]
  val testFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo/test_fix.py")
  testFile.write("""import sys
sys.path.insert(0, '/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo')

import django
from django.conf import settings
settings.configure(
    DEBUG=True,
    DATABASES={'default': {'ENGINE': 'django.db.backends.sqlite3', 'NAME': ':memory:'}},
    INSTALLED_APPS=['django.contrib.contenttypes', 'django.contrib.auth'],
)
django.setup()

from django.core.management.base import BaseCommand, DjangoHelpFormatter
from argparse import RawDescriptionHelpFormatter

print(f"DjangoHelpFormatter bases: {DjangoHelpFormatter.__bases__}")
print(f"Expected: ({RawDescriptionHelpFormatter.__name__},)")
print(f"Is subclass of RawDescriptionHelpFormatter: {issubclass(DjangoHelpFormatter, RawDescriptionHelpFormatter)}")
""")
  println("Test file written")
}