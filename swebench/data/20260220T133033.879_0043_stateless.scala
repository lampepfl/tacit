requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13925/repo") {
  val filePath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13925/repo/django/db/models/base.py"
  val content = access(filePath).read()
  // Let me find the exact string to replace
  val target = "cls._meta.pk.auto_created and\n            not settings.is_overridden('DEFAULT_AUTO_FIELD') and\n            not cls._meta.app_config._is_default_auto_field_overridden\n        ):"
  val replacement = "cls._meta.pk.auto_created and\n            not settings.is_overridden('DEFAULT_AUTO_FIELD') and\n            not cls._meta.app_config._is_default_auto_field_overridden and\n            not getattr(cls._meta.pk.remote_field, 'parent_link', False)\n        ):"
  val newContent = content.replace(target, replacement)
  access(filePath).write(newContent)
  "Done"
}(using implicitly[IOCapability])