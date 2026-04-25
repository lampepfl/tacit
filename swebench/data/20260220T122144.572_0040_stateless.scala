requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11910/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11910/repo/django/db/migrations/autodetector.py")
  val content = f.read()
  // Find the exact line number by searching for the buggy pattern
  val idx = content.indexOf("if to_field_rename_key in self.renamed_fields:")
  println(s"Index of 'if to_field_rename_key': $idx")
  if idx >= 0 then
    val before = content.substring(0, idx)
    val lineNum = before.count(c => c == '\n') + 1
    println(s"Line number: $lineNum")
    
    // Show the lines around this
    val lines = content.split("\n")
    println(s"Total lines: ${lines.length}")
    println(s"Line $lineNum: ${lines(lineNum-1)}")
    println(s"Line ${lineNum+1}: ${lines(lineNum)}")
}