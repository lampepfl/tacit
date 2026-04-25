requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15498/repo") {
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-15498/repo/django/views/static.py")
  val lines = f.readLines().toList.toArray
  
  // Modify line 138 (index 137) to include TypeError
  val newLines = lines.updated(137, "    except (AttributeError, ValueError, OverflowError, TypeError):")
  
  // Write the file back
  f.write(newLines.mkString("\n"))
  
  println("File updated successfully")
}