requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12983/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12983/repo/django/utils/text.py")
  val lines = f.readLines()
  // Print lines around 394 where slugify is defined (let's see lines 390-420)
  lines.slice(389, 420).zipWithIndex.foreach { case (line, idx) => println(s"${idx + 390}: $line") }
}