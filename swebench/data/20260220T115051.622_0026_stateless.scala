{
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11019/repo") {
    val content = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11019/repo/django/forms/widgets.py").read()
    val lines = content.split("\n")
    println("=== Lines 50-100 ===")
    (49 until 100).foreach(i => println(s"$i: ${lines(i)}"))
  }
}