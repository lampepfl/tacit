{
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11019/repo") {
    val content = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11019/repo/django/forms/widgets.py").read()
    val lines = content.split("\n").toBuffer
    
    // The fix should use elem_index < index instead of <= last_insert_index
    val idx = lines.indexWhere(_.contains("if elem_index <= last_insert_index:"))
    println(s"Found line: $idx -> ${lines(idx)}")
    
    lines(idx) = "                            if elem_index < index:"
    
    // Write back
    val modifiedContent = lines.mkString("\n")
    access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11019/repo/django/forms/widgets.py").write(modifiedContent)
    
    println("Fixed!")
  }
}