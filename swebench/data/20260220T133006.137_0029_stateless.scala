requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13964/repo") { 
  val lines = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13964/repo/django/db/models/base.py").readLines()
  val finalLines = scala.collection.mutable.ListBuffer[String]()
  var i = 0
  while (i < lines.length) {
    val line = lines(i)
    if (line.contains("if getattr(obj, field.target_field.attname) != getattr(self, field.attname):")) {
      finalLines.addOne(line)
      finalLines.addOne("                    setattr(self, field.attname, getattr(obj, field.target_field.attname))")
      // Skip the next line which is "field.delete_cached_value(self)"
      i += 1
      if (i < lines.length && lines(i).trim == "field.delete_cached_value(self)") {
        finalLines.addOne(lines(i))
      }
    } else {
      finalLines.addOne(line)
    }
    i += 1
  }
  // Write the modified content
  access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13964/repo/django/db/models/base.py").write(finalLines.mkString("\n"))
  println("File updated successfully")
}