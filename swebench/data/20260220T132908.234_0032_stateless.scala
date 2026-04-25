{
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13933/repo") {
    val modelsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13933/repo/django/forms/models.py")
    val content = modelsFile.read()
    val lines = content.split("\n").toList
    
    // Fix: Remove both lines 1191-1192 and replace with correct 3-line block
    // Current (broken):
    // 1191:         'invalid_choice': _('Select a valid choice. That choice is not one of'
    // 1192:         'invalid_choice': _('Select a valid choice. %(value)s is not one of the'
    // 1193:                             ' available choices.'),
    
    // We want:
    // 1191:         'invalid_choice': _('Select a valid choice. %(value)s is not one of the'
    // 1192:                             ' available choices.'),
    // 1193:     }
    
    // First, remove lines 1191 and 1192 (0-indexed: 1190, 1191)
    val newLines1 = lines.patch(1190, Nil, 2)
    
    // Now add the correct lines at position 1190
    val correctLine1 = "        'invalid_choice': _('Select a valid choice. %(value)s is not one of the'"
    val correctLine2 = "                            ' available choices.'),"
    val correctLine3 = "    }"
    
    val newLines2 = newLines1.patch(1190, List(correctLine1, correctLine2, correctLine3), 0)
    
    // Write the fixed content
    val newContent = newLines2.mkString("\n")
    modelsFile.write(newContent)
    println("Fixed error messages section")
  }
}
