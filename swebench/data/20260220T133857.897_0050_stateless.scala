{
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13933/repo") {
    val modelsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13933/repo/django/forms/models.py")
    val content = modelsFile.read()
    val lines = content.split("\n")
    
    // Compare with ModelMultipleChoiceField to ensure consistency
    println("=== ModelMultipleChoiceField default_error_messages ===")
    val mmcfLine = lines.indexWhere(_.contains("class ModelMultipleChoiceField"))
    lines.slice(mmcfLine, mmcfLine + 12).zipWithIndex.foreach { case (line, idx) =>
      println(s"${mmcfLine + idx + 1}: $line")
    }
    
    // Show _check_values in ModelMultipleChoiceField to compare with to_python
    println("\n=== ModelMultipleChoiceField._check_values (invalid_choice part) ===")
    val checkValuesLine = lines.indexWhere(_.contains("def _check_values"))
    lines.slice(checkValuesLine + 20, checkValuesLine + 30).zipWithIndex.foreach { case (line, idx) =>
      println(s"${checkValuesLine + 21 + idx}: $line")
    }
  }
}
