
import scala.collection.mutable

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12708/repo") {
  val schemaFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12708/repo/django/db/backends/base/schema.py")
  val lines = schemaFile.readLines().to(mutable.ArrayBuffer)
  
  // Change line 396: add 'unique': False to exclude unique constraints
  val oldLine = 395  // 0-indexed, so line 396 - 1
  val newLineContent = "            self._delete_composed_index(model, fields, {'index': True, 'unique': False}, self.sql_delete_index)"
  
  println(s"Old line ${oldLine+1}: ${lines(oldLine)}")
  lines(oldLine) = newLineContent
  println(s"New line ${oldLine+1}: ${lines(oldLine)}")
  
  // Write back
  schemaFile.write(lines.mkString("\n"))
  println("File updated successfully!")
}
