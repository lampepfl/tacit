requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15388/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15388/repo/tests/utils_tests/test_autoreload.py")
  val lines = f.readLines()
  val common_roots_lines = lines.zipWithIndex.filter { case (line, idx) => line.contains("common_roots") }.map { case (line, idx) => (idx + 1, line) }
  common_roots_lines
}