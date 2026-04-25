{
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13933/repo") {
    val modelsFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13933/repo/django/forms/models.py")
    val content = modelsFile.read()
    val lines = content.split("\n")
    
    // Show the invalid_choice raise in _check_values
    println("=== ModelMultipleChoiceField._check_values (invalid_choice) ===")
    val checkValuesLine = lines.indexWhere(_.contains("def _check_values"))
    lines.slice(checkValuesLine + 23, checkValuesLine + 30).zipWithIndex.foreach { case (line, idx) =>
      println(s"${checkValuesLine + 24 + idx}: $line")
    }
  }
}
