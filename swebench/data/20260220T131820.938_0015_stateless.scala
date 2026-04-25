
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13447/repo") {
  val sitesFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13447/repo/django/contrib/admin/sites.py")
  val lines = sitesFile.readLines().toList
  
  // Find line 467 (0-indexed) and add 'model': model after it
  val modifiedLines = lines.zipWithIndex.map { case (line, idx) =>
    if (idx == 467 && line.trim == "'add_url': None,") {
      // This is the line after 'add_url': None,
      "                'model': model,"
    } else {
      line
    }
  }
  
  sitesFile.write(modifiedLines.mkString("\n"))
  println("Done")
}
