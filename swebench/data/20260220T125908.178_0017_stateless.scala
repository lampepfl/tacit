requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12856/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12856/repo/django/db/models/base.py")
  val lines = f.readLines().toList
  
  // Find the line with "def _check_constraints(cls, databases):"
  val startIdx = lines.indexWhere(_.contains("def _check_constraints(cls, databases):"))
  println(s"Found _check_constraints at line $startIdx")
  
  // The line after "def _check_constraints..." is "        errors = []" 
  // Find the line "        errors = []" 
  val errorsLineIdx = lines.indexWhere(_.trim == "errors = []", startIdx)
  println(s"Found 'errors = []' at line $errorsLineIdx")
  
  // Find the next line after "errors = []"
  val nextLineIdx = errorsLineIdx + 1
  println(s"Next line: ${lines(nextLineIdx)}")
  
  // Insert our validation code after errors = []
  val validationCode = """
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
  
  val newLines = lines.patch(nextLineIdx, List(validationCode.stripMargin), 0)
  val content = newLines.mkString("\n")
  f.write(content)
  println("Done")
}