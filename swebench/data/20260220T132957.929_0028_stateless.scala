requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13964/repo") { 
  val lines = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13964/repo/django/db/models/base.py").readLines()
  // Need to find the exact line to modify
  val newLines = lines.zipWithIndex.map { case (line, idx) =>
    if (line.contains("if getattr(obj, field.target_field.attname) != getattr(self, field.attname):")) {
      "                if getattr(obj, field.target_field.attname) != getattr(self, field.attname):\n                    setattr(self, field.attname, getattr(obj, field.target_field.attname))"
    } else {
      line
    }
  }
  // The line gets split, need to handle differently
  val finalLines = scala.collection.mutable.ListBuffer[String]()
  var i = 0
  while (i < lines.length) {
    val line = lines(i)
    if (line.contains("if getattr(obj, field.target_field.attname) != getattr(self, field.attname):")) {
      finalLines.addOne(line)
      finalLines.addOne("                    setattr(self, field.attname, getattr(obj, field.target_field.attname))")
    } else {
      finalLines.addOne(line)
    }
    i += 1
  }
  finalLines.mkString("\n")
}