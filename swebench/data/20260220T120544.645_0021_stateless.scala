requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11742/repo") { 
  val fieldsInit = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11742/repo/django/db/models/fields/__init__.py")
  val content = fieldsInit.read()
  val lines = content.split("\n").toList
  
  // Modify the check method (line 954-958) to add the new check
  val newCheckLines = lines.updated(953, "    def check(self, **kwargs):").updated(954, "        return [").updated(955, "            *super().check(**kwargs),").updated(956, "            *self._check_max_length_attribute(**kwargs),").updated(957, "            *self._check_max_length_choices(**kwargs),").updated(958, "        ]")
  
  // Now add the new method after line 979 (after _check_max_length_attribute)
  val newMethod = """
    def _check_max_length_choices(self):
        if not self.choices:
            return []

        if self.max_length is None:
            return []

        if not isinstance(self.max_length, int) or isinstance(self.max_length, bool) or self.max_length <= 0:
            return []

        max_choice_length = 0
        for choice in self.choices:
            try:
                group_name, group_choices = choice
                if isinstance(group_choices, (list, tuple)):
                    for value, _ in group_choices:
                        if value is None:
                            continue
                        value_str = str(value) if isinstance(value, Promise) else str(value)
                        max_choice_length = max(max_choice_length, len(value_str))
                else:
                    value = group_name
                    value_str = str(value) if isinstance(value, Promise) else str(value)
                    max_choice_length = max(max_choice_length, len(value_str))
            except (TypeError, ValueError):
                value, _ = choice
                if value is None:
                    continue
                value_str = str(value) if isinstance(value, Promise) else str(value)
                max_choice_length = max(max_choice_length, len(value_str))

        if max_choice_length > self.max_length:
            return [
                checks.Error(
                    "max_length must be at least %d to accommodate the longest choice value (%d)." % (max_choice_length, max_choice_length),
                    obj=self,
                    id='fields.E122',
                )
            ]
        return []
"""
  
  // Insert the new method after line 979 (index 979)
  val insertIndex = 979
  val before = newCheckLines.take(insertIndex + 1)
  val after = newCheckLines.drop(insertIndex + 1)
  val newMethodList = newMethod.split("\n").filter(_.nonEmpty).toList.map(s => if s.startsWith("    ") then s else "    " + s)
  val allLines = before ::: newMethodList ::: after
  
  val newContent = allLines.mkString("\n")
  
  // Write the file
  fieldsInit.write(newContent)
  println("File updated successfully")
}