requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14608/repo") {
  val formsets = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14608/repo/django/forms/formsets.py")
  val lines = formsets.readLines()
  // Print lines around 335-385
  (333 to 385).foreach { idx =>
    println(s"${idx + 1}: ${lines(idx)}")
  }
}
