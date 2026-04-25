requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12453/repo") {
  val f = access("django/db/backends/base/creation.py")
  val lines = f.readLines()
  
  println("Lines 1-10:")
  (0 until 10).foreach(i => println(s"${i+1}: ${lines(i)}"))
  
  println("\nLines 123-134:")
  (122 until 134).foreach(i => println(s"${i+1}: ${lines(i)}"))
}