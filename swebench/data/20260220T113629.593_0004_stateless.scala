requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-10914/repo") {
  // Read the global_settings.py file to get all the content
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-10914/repo/django/conf/global_settings.py")
  val content = f.read()
  // Find and replace FILE_UPLOAD_PERMISSIONS = None with FILE_UPLOAD_PERMISSIONS = 0o644
  val newContent = content.replace("FILE_UPLOAD_PERMISSIONS = None", "FILE_UPLOAD_PERMISSIONS = 0o644")
  // Write the new content
  f.write(newContent)
  println("Done")
}