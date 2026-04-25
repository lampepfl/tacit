requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo") { 
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13315/repo/django/forms/models.py")
  val content = file.read()
  val newContent = content.replace(
    "formfield.queryset = formfield.queryset.complex_filter(limit_choices_to)",
    "formfield.queryset = formfield.queryset.complex_filter(limit_choices_to).distinct()"
  )
  file.write(newContent)
  println("File updated successfully")
}