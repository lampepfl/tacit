requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo") {
  val fs = summon[FileSystem]
  val testFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo/test_fix.py")
  val line1 = "import sys\n"
  val line2 = "sys.path.insert(0, '/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo')\n"
  val line3 = "\n"
  val line4 = "import django\n"
  val line5 = "from django.conf import settings\n"
  val line6 = "settings.configure(\n"
  val line7 = "    DEBUG=True,\n"
  val line8 = "    DATABASES={'default': {'ENGINE': 'django.db.backends.sqlite3', 'NAME': ':memory:'}},\n"
  val line9 = "    INSTALLED_APPS=['django.contrib.contenttypes', 'django.contrib.auth'],\n"
  val line10 = ")\n"
  val line11 = "django.setup()\n"
  val line12 = "\n"
  val line13 = "from django.core.management.base import BaseCommand, DjangoHelpFormatter\n"
  val line14 = "from argparse import RawDescriptionHelpFormatter\n"
  val line15 = "\n"
  val line16 = "print(f\"DjangoHelpFormatter bases: {DjangoHelpFormatter.__bases__}\")\n"
  val line17 = "print(f\"Expected: ({RawDescriptionHelpFormatter.__name__},)\")\n"
  val line18 = "print(f\"Is subclass of RawDescriptionHelpFormatter: {issubclass(DjangoHelpFormatter, RawDescriptionHelpFormatter)}\")\n"
  testFile.write(line1 + line2 + line3 + line4 + line5 + line6 + line7 + line8 + line9 + line10 + line11 + line12 + line13 + line14 + line15 + line16 + line17 + line18)
  println("Written")
}