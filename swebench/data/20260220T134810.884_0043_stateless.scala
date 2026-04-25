requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo/tests/model_fields/test_manytomanyfield.py")
  val lines = file.readLines()
  lines.take(50).zipWithIndex.foreach { case (line, i) =>
    println(s"${i + 1}: $line")
  }
}
