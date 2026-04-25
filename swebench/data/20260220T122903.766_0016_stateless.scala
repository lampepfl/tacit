requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo") {
  given fs: FileSystem = summon[FileSystem]
  given perm: ProcessPermission = summon[ProcessPermission]
  
  // Test if the fix works by importing the function
  val result = exec("python", List("-c", 
    "import django; django.setup(); " +
    "from django.contrib.admin.utils import display_for_field; " +
    "from django.db import models; " +
    "field = models.JSONField(); " +
    "value = {'foo': 'bar'}; " +
    "result = display_for_field(value, field, '-'); " +
    "print(repr(result))"
  ), Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo"), 30000)
  
  println(s"Exit code: ${result.exitCode}")
  println(s"Stdout: ${result.stdout}")
  println(s"Stderr: ${result.stderr}")
}
