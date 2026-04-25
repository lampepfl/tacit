requestFileSystem[Unit]("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12747/repo") { (fs: FileSystem) ?=>
  // Now let's add the code to track models when objs is empty
  val delFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12747/repo/django/db/models/deletion.py")
  val lines = delFile.readLines()
  
  val newLines = scala.collection.mutable.ListBuffer[String]()
  
  // Add all lines up to line 242 (the end of docstring, which is line 242 in 1-indexed)
  // In 0-indexed, line 242 is index 241
  for (i <- 0 until 242) {
    newLines.addOne(lines(i))
  }
  
  // Add the new code to track empty querysets
  newLines.addOne("        # Track the model when objs is empty to ensure consistent return value")
  newLines.addOne("        if not objs and source is None:")
  newLines.addOne("            if hasattr(objs, '_meta'):")
  newLines.addOne("                model = objs._meta.model")
  newLines.addOne("            elif hasattr(objs, 'model'):")
  newLines.addOne("                model = objs.model")
  newLines.addOne("            else:")
  newLines.addOne("                model = None")
  newLines.addOne("            if model is not None:")
  newLines.addOne("                self._collected_models.add(model)")
  
  // Add the rest of the lines from 242 onwards
  for (i <- 242 until lines.length) {
    newLines.addOne(lines(i))
  }
  
  val modified = newLines.mkString("\n")
  
  // Write the modified content
  access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12747/repo/django/db/models/deletion.py").write(modified)
  
  println("Added tracking code in collect method")
}