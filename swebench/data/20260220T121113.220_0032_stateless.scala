requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11742/repo") { 
  val fieldsInit = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11742/repo/django/db/models/fields/__init__.py")
  val lines = fieldsInit.readLines()
  
  // Update the check method to include new check
  val newLines = lines.updated(956, "            *self._check_max_length_attribute(**kwargs),").::(958, "*self._check_max_length_choices(**kwargs),")
  
  // Add the new method
  val methodToAdd = List(
    "",
    "    def _check_max_length_choices(self):",
    "        if not self.choices:",
    "            return []",
    "",
    "        if self.max_length is None:",
    "            return []",
    "",
    "        if not isinstance(self.max_length, int) or isinstance(self.max_length, bool) or self.max_length <= 0:",
    "            return []",
    "",
    "        max_choice_length = 0",
    "        for choice in self.choices:",
    "            try:",
    "                group_name, group_choices = choice",
    "                if isinstance(group_choices, (list, tuple)):",
    "                    for value, _ in group_choices:",
    "                        if value is None:",
    "                            continue",
    "                        value_str = str(value) if isinstance(value, Promise) else str(value)",
    "                        max_choice_length = max(max_choice_length, len(value_str))",
    "                else:",
    "                    value = group_name",
    "                    value_str = str(value) if isinstance(value, Promise) else str(value)",
    "                    max_choice_length = max(max_choice_length, len(value_str))",
    "            except (TypeError, ValueError):",
    "                value, _ = choice",
    "                if value is None:",
    "                    continue",
    "                value_str = str(value) if isinstance(value, Promise) else str(value)",
    "                max_choice_length = max(max_choice_length, len(value_str))",
    "",
    "        if max_choice_length > self.max_length:",
    "            return [",
    "                checks.Error(",
    "                    \"max_length must be at least %d to accommodate the longest choice value (%d).\" % (max_choice_length, max_choice_length),",
    "                    obj=self,",
    "                    id='fields.E122',",
    "                )",
    "            ]",
    "        return []",
    ""
  )
  
  // Insert the method after line 979 (index 979)
  val insertIndex = 979
  val before = newLines.take(insertIndex + 1)
  val after = newLines.drop(insertIndex + 1)
  val allLines = before ::: methodToAdd ::: after
  
  val newContent = allLines.mkString("\n")
  fieldsInit.write(newContent)
  println("File updated successfully")
}