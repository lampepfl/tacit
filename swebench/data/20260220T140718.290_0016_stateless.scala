requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo") {
  val fs = summon[FileSystem]
  val base = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15781/repo/django/core/management/base.py")
  val content = base.read()
  val lines = content.split("\n").toVector
  
  // Make the changes:
  // 1. Change line 8: from argparse import ArgumentParser, HelpFormatter
  // 2. Change line 105: class DjangoHelpFormatter(HelpFormatter):
  
  val newLines = lines.updated(7, "from argparse import ArgumentParser, RawDescriptionHelpFormatter").updated(104, "class DjangoHelpFormatter(RawDescriptionHelpFormatter):")
  
  val newContent = newLines.mkString("\n")
  base.write(newContent)
  println("Done")
}