requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo/django/db/models/fields/related.py")
  val lines = file.readLines().toArray
  
  // First modification: Add has_related_name in __init__ (after line 1198)
  val initLineIndex = 1197  // 0-indexed, line 1198
  val modifiedLines1 = lines.updated(initLineIndex, lines(initLineIndex) + "\n        self.has_related_name = related_name is not None")
  
  // Second modification: Update _check_ignored_options method
  // Find the method signature line
  var methodLineIndex = -1
  for (i <- modifiedLines1.indices) {
    if (modifiedLines1(i).contains("def _check_ignored_options")) {
      methodLineIndex = i
    }
  }
  println(s"_check_ignored_options found at line ${methodLineIndex + 1}")
  
  // The method currently returns warnings. We need to change it to also return errors.
  // Let's look at what we need to add:
  // - Add from_model=None parameter
  // - Add error check for related_name on symmetrical fields
  
  // Updated method signature
  val newMethodSignature = "    def _check_ignored_options(self, from_model=None, **kwargs):"
  val modifiedLines2 = modifiedLines1.updated(methodLineIndex, newMethodSignature)
  
  // Now we need to add the check for related_name
  // Find where to insert it - after the last warning check (before "return warnings")
  // The current return is at line 1261 (0-indexed: 1260)
  // Actually let's find it dynamically
  var returnIndex = -1
  for (i <- methodLineIndex until modifiedLines2.length) {
    if (modifiedLines2(i).trim == "return warnings") {
      returnIndex = i
    }
  }
  println(s"return warnings found at line ${returnIndex + 1}")
  
  // The check to add before return:
  // if from_model is not None and self.has_related_name and self.remote_field.symmetrical:
  //     if (self.remote_field.model == RECURSIVE_RELATIONSHIP_CONSTANT or 
  //         self.remote_field.model == from_model._meta.object_name):
  //         errors.append(...)
  
  // Actually, let's create a new list that includes both warnings and errors
  // We need to change "warnings = []" to "errors = []" and then handle both
  // But it's easier to just add a new errors list
  
  // Let's create the new code to insert before return
  val checkCode = """
        if (from_model is not None and self.has_related_name and 
                self.remote_field.symmetrical and
                self.remote_field.model != 'self' and 
                self.remote_field.model != from_model._meta.object_name):
            return warnings
        if from_model is not None and self.has_related_name and self.remote_field.symmetrical:
            if (
                self.remote_field.model == RECURSIVE_RELATIONSHIP_CONSTANT or
                self.remote_field.model == from_model._meta.object_name
            ):
                warnings.append(
                    checks.Error(
                        'related_name is not allowed for ManyToManyField '
                        'with symmetrical=True (e.g., self-referential fields).',
                        obj=self,
                        id='fields.E332',
                    )
                )
"""
  
  // This approach is complex. Let me just rewrite the method more carefully.
  // Actually, let me use a simpler approach - insert the check before "return warnings"
  
  // Build the complete new content
  val newLines = scala.collection.mutable.ListBuffer[String]()
  
  for (i <- modifiedLines2.indices) {
    newLines += modifiedLines2(i)
    if (i == methodLineIndex) {
      // Already added the new signature
    }
    if (i == returnIndex - 1) {
      // Insert the new check before return warnings
      newLines += "        if from_model is not None and self.has_related_name and self.remote_field.symmetrical:"
      newLines += "            if ("
      newLines += "                self.remote_field.model == RECURSIVE_RELATIONSHIP_CONSTANT or"
      newLines += "                self.remote_field.model == from_model._meta.object_name"
      newLines += "            ):"
      newLines += "                warnings.append("
      newLines += "                    checks.Error("
      newLines += "                        'related_name is not allowed for ManyToManyField '"
      newLines += "                        'with symmetrical=True (e.g., self-referential fields).',"
      newLines += "                        obj=self,"
      newLines += "                        id='fields.E332',"
      newLines += "                    )"
      newLines += "                )"
    }
  }
  
  val newContent = newLines.mkString("\n")
  
  // Write back to the file
  file.write(newContent)
  
  println("File modified successfully")
}
