
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14382/repo") {
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14382/repo/django/core/management/templates.py")
  val content = file.read()
  
  // Find the line to fix and replace it
  val fixedContent = content.replace(
    "self.validate_name(os.path.basename(target), 'directory')",
    "self.validate_name(os.path.basename(target.rstrip(os.sep)), 'directory')"
  )
  
  file.write(fixedContent)
  println("File updated successfully")
}
