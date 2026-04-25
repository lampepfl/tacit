requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  val adminUtils = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo/django/contrib/admin/utils.py")
  val content = adminUtils.read()
  
  val lines = content.split("\n").toList
  
  // Insert the JSONField case after line 400 (the FileField case) and before line 401 (else)
  val newLines = lines.slice(0, 400) ::: List("    elif isinstance(field, models.JSONField):") ::: List("        return field.formfield().prepare_value(value)") ::: lines.slice(400, lines.length)
  
  val newContent = newLines.mkString("\n")
  adminUtils.write(newContent)
  println("Done!")
}
