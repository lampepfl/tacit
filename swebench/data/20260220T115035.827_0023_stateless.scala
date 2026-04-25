{
  requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11019/repo") {
    val content = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11019/repo/django/forms/widgets.py").read()
    // Print lines around 30-50 and 140-180
    val lines = content.split("\n")
    println("=== Lines 30-50 ===")
    (29 until 50).foreach(i => println(s"$i: ${lines(i)}"))
    println("\n=== Lines 140-180 ===")
    (139 until 180).foreach(i => println(s"$i: ${lines(i)}"))
  }
}