requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14915/repo") { 
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14915/repo/django/forms/models.py")
  val lines = f.readLines()
  
  // Find the line with __eq__ and insert __hash__ after it
  val newLines = scala.collection.mutable.ListBuffer[String]()
  
  var i = 0
  while (i < lines.size) {
    newLines.addOne(lines(i))
    // If this is the line with "return self.value == other"
    if (lines(i).trim == "return self.value == other") {
      // Add the __hash__ method
      newLines.addOne("    def __hash__(self):")
      newLines.addOne("        return hash(self.value)")
    }
    i += 1
  }
  
  f.write(newLines.mkString("\n"))
  println("File updated successfully")
}