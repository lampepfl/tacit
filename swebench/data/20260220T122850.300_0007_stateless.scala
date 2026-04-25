requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12286/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12286/repo/django/core/checks/translation.py")
  val content = f.read()
  
  val oldCode = """def check_language_settings_consistent(app_configs, **kwargs):
    \"\"\"Error if language settings are not consistent with each other.\"\"\"
    available_tags = {i for i, _ in settings.LANGUAGES} | {'en-us'}
    if settings.LANGUAGE_CODE not in available_tags:
        return [E004]
    return []"""

  val newCode = """def check_language_settings_consistent(app_configs, **kwargs):
    \"\"\"Error if language settings are not consistent with each other.\"\"\"
    available_tags = {i for i, _ in settings.LANGUAGES} | {'en-us'}
    if settings.LANGUAGE_CODE not in available_tags:
        if settings.LANGUAGE_CODE.split('-')[0] not in available_tags:
            return [E004]
    return []"""
  
  val newContent = content.replace(oldCode, newCode)
  f.write(newContent)
  println("File updated successfully")
}
