requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14580/repo") {
  val serializer = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14580/repo/django/db/migrations/serializer.py")
  val content = serializer.read()
  
  // Find the line with special_cases and fix it
  val fixedContent = content.replace(
    "(models.Model, \"models.Model\", [])",
    "(models.Model, \"models.Model\", [\"from django.db import models\"])"
  )
  
  serializer.write(fixedContent)
  println("Fixed!")
}