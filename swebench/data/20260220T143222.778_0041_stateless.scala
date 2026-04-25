requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo") { 
  val autoreload = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo/django/utils/autoreload.py")
  val content = autoreload.read()
  // Show lines around the problem
  val lines = content.split("\n").zipWithIndex
  lines.slice(218, 235).foreach { (line, idx) => println(s"$idx: '$line'") }
}