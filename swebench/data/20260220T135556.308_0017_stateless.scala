requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14915/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14915/repo/django/forms/models.py")
  val content = f.read()
  
  // Find and remove duplicates
  val lines = content.split("\n").toList
  
  // Filter out duplicate __hash__ lines - keep only one
  val newLines = scala.collection.mutable.ListBuffer[String]()
  var skipNext = false
  
  var i = 0
  while (i < lines.size) {
    if (skipNext) {
      skipNext = false
    } else if (lines(i).trim == "def __hash__(self):" && i + 1 < lines.size && lines(i+1).trim == "return hash(self.value)") {
      // Check if next line is also __hash__ (duplicate)
      if (i + 2 < lines.size && lines(i+2).trim == "def __hash__(self):") {
        // This is a duplicate, skip both
        newLines.addOne(lines(i))
        newLines.addOne(lines(i+1))
        skipNext = true
        i += 2 // Skip the duplicate pair
      } else {
        newLines.addOne(lines(i))
      }
    } else {
      newLines.addOne(lines(i))
    }
    i += 1
  }
  
  f.write(newLines.mkString("\n"))
  println("File fixed")
}