
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo/django/db/migrations/autodetector.py")
  val lines = f.readLines()
  
  // Find generate_added_indexes
  for (i <- 0 until lines.length) {
    if (lines(i).contains("def generate_added_indexes")) {
      println(s"Found generate_added_indexes at line ${i + 1}")
      for (j <- i until i + 15) {
        println(s"${j + 1}: ${lines(j)}")
      }
    }
  }
}
