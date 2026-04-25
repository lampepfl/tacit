
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo/django/db/migrations/autodetector.py")
  val lines = f.readLines()
  
  // Find the exact line with "order_wrt_unset"
  for (i <- 0 until lines.length) {
    if (lines(i).contains("order_wrt_unset")) {
      println(s"Found at line ${i + 1}")
      println(s"Context: ${lines(i-2)}")
      println(s"Context: ${lines(i-1)}")
      println(s"Line: ${lines(i)}")
      println(s"Context: ${lines(i+1)}")
      println(s"Context: ${lines(i+2)}")
      println(s"Context: ${lines(i+3)}")
      println(s"Context: ${lines(i+4)}")
      println(s"Context: ${lines(i+5)}")
    }
  }
}
