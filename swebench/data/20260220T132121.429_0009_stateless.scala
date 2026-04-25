requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13658/repo/django/core/management") { 
  val initFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13658/repo/django/core/management/__init__.py")
  val content = initFile.read()
  val newContent = content.replace(
    "parser = CommandParser(usage='%(prog)s subcommand [options] [args]', add_help=False, allow_abbrev=False)",
    "parser = CommandParser(prog=self.prog_name, usage='%(prog)s subcommand [options] [args]', add_help=False, allow_abbrev=False)"
  )
  initFile.write(newContent)
  println("File updated successfully")
}
