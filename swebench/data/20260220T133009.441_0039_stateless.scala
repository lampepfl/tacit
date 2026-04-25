requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13925/repo") {
  val filePath = "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13925/repo/django/db/models/base.py"
  val content = access(filePath).read()
  val oldCode = """    @classmethod
    def _check_default_pk(cls):
        if (
            cls._meta.pk.auto_created and
            not settings.is_overridden('DEFAULT_AUTO_FIELD') and
            not cls._meta.app_config._is_default_auto_field_overridden
        ):"""
  val newCode = """    @classmethod
    def _check_default_pk(cls):
        if (
            cls._meta.pk.auto_created and
            not settings.is_overridden('DEFAULT_AUTO_FIELD') and
            not cls._meta.app_config._is_default_auto_field_overridden and
            not getattr(cls._meta.pk.remote_field, 'parent_link', False)
        ):"""
  val newContent = content.replace(oldCode, newCode)
  access(filePath).write(newContent)
  "Done"
}(using implicitly[IOCapability])