requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo") {
  val fs = summon[FileSystem]
  val testFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo/test_fix.py")
  // Write the file line by line
  testFile.write("import sys\n")
  testFile.write("sys.path.insert(0, '/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo')\n")
  testFile.write("\n")
  testFile.write("import django\n")
  testFile.write("from django.conf import settings\n")
  testFile.write("settings.configure(\n")
  testFile.write("    DEBUG=True,\n")
  testFile.write("    DATABASES={'default': {'ENGINE': 'django.db.backends.sqlite3', 'NAME': ':memory:'}},\n")
  testFile.write("    INSTALLED_APPS=['django.contrib.contenttypes', 'django.contrib.auth'],\n")
  testFile.write(")\n")
  testFile.write("django.setup()\n")
  testFile.write("\n")
  testFile.write("from django.core.management.base import BaseCommand, DjangoHelpFormatter\n")
  testFile.write("from argparse import RawDescriptionHelpFormatter\n")
  testFile.write("\n")
  testFile.write("print(f\"DjangoHelpFormatter bases: {DjangoHelpFormatter.__bases__}\")\n")
  testFile.write("print(f\"Expected: ({RawDescriptionHelpFormatter.__name__},)\")\n")
  testFile.write("print(f\"Is subclass of RawDescriptionHelpFormatter: {issubclass(DjangoHelpFormatter, RawDescriptionHelpFormatter)}\")\n")
  println("Done")
}