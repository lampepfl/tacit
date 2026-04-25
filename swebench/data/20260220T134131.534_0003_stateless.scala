requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14534/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14534/repo/django/forms/boundfield.py")
  val content = f.read()
  val lines = content.split("\n").toList
  
  // Find the line with id_for_label in BoundWidget
  val newLines = lines.zipWithIndex.map { case (line, idx) =>
    if (line.contains("return 'id_%s_%s' % (self.data['name'], self.data['index'])")) {
      "        return self.data['attrs']['id']"
    } else line
  }
  
  val newContent = newLines.mkString("\n")
  f.write(newContent)
  println("File updated successfully")
}