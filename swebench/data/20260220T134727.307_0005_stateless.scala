
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14855/repo") {
  val fs: FileSystem^ = summon
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14855/repo/django/contrib/admin/helpers.py")
  val content = file.read()
  val lines = content.split("\n")
  // Modify line 212 (index 211) to add current_app parameter
  val modifiedLines = lines.updated(211, "            url = reverse(url_name, args=[quote(remote_obj.pk)], current_app=self.model_admin.admin_site.name)")
  val modifiedContent = modifiedLines.mkString("\n")
  file.write(modifiedContent)
  println("File modified successfully")
}
