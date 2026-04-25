requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo") { 
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14730/repo/django/db/models/fields/related.py")
  val lines = file.readLines()
  
  // Find ManyToManyField's check method
  var m2mCheckLine = -1
  for (i <- lines.indices) {
    if (lines(i).contains("class ManyToManyField")) {
      // Find the check method after this class
      for (j <- i until (i + 100).min(lines.length)) {
        if (lines(j).contains("def check(") && !lines(j).contains("def check(") && j > i + 10) {
          m2mCheckLine = j
        }
      }
    }
  }
  
  // Let's do it differently - search for ManyToManyField's check by looking for "def check" after line 1145
  val startLine = 1145
  for (i <- startLine until (startLine + 80).min(lines.length)) {
    if (lines(i).trim == "def check(self, **kwargs):") {
      println(s"ManyToManyField.check found at line ${i + 1}")
      lines.drop(i).take(12).zipWithIndex.foreach { case (line, j) =>
        println(s"${i + j + 1}: $line")
      }
    }
  }
}
