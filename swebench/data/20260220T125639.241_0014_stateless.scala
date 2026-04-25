requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12856/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12856/repo/django/db/models/base.py")
  val lines = f.readLines().toList
  val content = lines.mkString("\n")
  // Now find where to insert - after "def _check_constraints(cls, databases):" and "errors = []"
  // The new code should go after line 1867 (errors = [])
  
  // Let's insert the validation code after line 1867
  val before = content.substring(0, content.indexOf("    @classmethod\n    def _check_constraints(cls, databases):"))
  val rest = content.substring(content.indexOf("    @classmethod\n    def _check_constraints(cls, databases):") + 
    "    @classmethod\n    def _check_constraints(cls, databases):".length)
  
  // We need to find the "errors = []" line and add after it
  val newMethod = """
    @classmethod
    def _check_constraints(cls, databases):
        errors = []
        # Validate fields in UniqueConstraint
        from django.db import models
        for constraint in cls._meta.constraints:
            if isinstance(constraint, UniqueConstraint):
                for field_name in constraint.fields:
                    if field_name not in cls._meta._forward_fields_map:
                        errors.append(
                            checks.Error(
                                "'%s' refers to the nonexistent field '%s'." % (
                                    'constraints', field_name,
                                ),
                                obj=cls,
                                id='models.E012',
                            )
                        )
                    else:
                        field = cls._meta._forward_fields_map[field_name]
                        if isinstance(field.remote_field, models.ManyToManyRel):
                            errors.append(
                                checks.Error(
                                    "'%s' refers to a ManyToManyField '%s', but "
                                    "ManyToManyFields are not permitted in '%s'." % (
                                        'constraints', field_name, 'constraints',
                                    ),
                                    obj=cls,
                                    id='models.E013',
                                )
                            )
"""
  val finalContent = before + newMethod + rest
  // Write to file
  access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12856/repo/django/db/models/base.py").write(finalContent)
  println("Done writing")
}