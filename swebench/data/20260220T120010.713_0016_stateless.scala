requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo") {
  val resolvers = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11620/repo/django/urls/resolvers.py")
  val content = resolvers.read()
  // Modify the imports to add Http404
  val newContent = content.replace(
    "from django.core.exceptions import ImproperlyConfigured, ViewDoesNotExist",
    "from django.core.exceptions import ImproperlyConfigured, ViewDoesNotExist\nfrom django.http import Http404"
  )
  resolvers.write(newContent)
  println("Import added")
}