requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12286/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12286/repo/django/core/checks/translation.py")
  val content = f.read()
  
  // Check if function looks correct
  if (content.contains("def check_language_settings_consistent") && 
      content.contains("settings.LANGUAGE_CODE.split('-')[0]")) {
    println("Fix applied correctly!")
  } else {
    println("ERROR: Fix not applied correctly")
  }
}
