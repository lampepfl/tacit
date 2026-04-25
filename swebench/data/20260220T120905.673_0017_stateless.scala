requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11848/repo") {
  val f = access("django/utils/http.py")
  val content = f.read()
  
  // Use lines array to get exact string
  val lines = content.split("\n")
  val line176 = lines(176)
  val line177 = lines(177)
  
  // Find this specific occurrence
  val idx = content.indexOfSlice(line176)
  println(s"Index of line 176: $idx")
  
  // Now replace using the exact lines from the file
  val oldCodeFromFile = line176 + "\n" + line177 + "\n" + lines(178) + "\n" + lines(179) + "\n" + lines(180) + "\n" + lines(181) + "\n" + lines(182)
  println(s"Old code from file:")
  println(oldCodeFromFile)
}