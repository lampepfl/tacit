
requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo") {
  given fs: FileSystem = summon[FileSystem]
  
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13265/repo/django/db/migrations/autodetector.py")
  val lines = f.readLines()
  
  // Create new content
  val newLines = new scala.collection.mutable.ListBuffer[String]()
  
  var i = 0
  while (i < lines.length) {
    val line = lines(i)
    
    // Check if we need to insert new code after "order_wrt_unset" case
    if (line.contains("order_wrt_unset") && line.contains("elif dependency")) {
      // Add the current line
      newLines += line
      i += 1
      
      // Add lines until we reach "# Field is removed"
      while (i < lines.length && !lines(i).trim.startsWith("# Field is removed")) {
        newLines += lines(i)
        i += 1
      }
      
      // Now insert the new dependency check before "# Field is removed"
      newLines += "        # order_with_respect_to being set for a field"
      newLines += "        elif dependency[2] is not None and dependency[3] == \"order_wrt_set\":"
      newLines += "            return ("
      newLines += "                isinstance(operation, operations.AlterOrderWithRespectTo) and"
      newLines += "                operation.name_lower == dependency[1].lower() and"
      newLines += "                operation.order_with_respect_to is not None and"
      newLines += "                operation.order_with_respect_to.lower() == dependency[2].lower()"
      newLines += "            )"
      // Continue - the loop will add "# Field is removed" in next iteration
    } else {
      newLines += line
      i += 1
    }
  }
  
  // Check if the modification was applied
  val content = newLines.mkString("\n")
  if (content.contains("order_wrt_set")) {
    println("First modification applied successfully")
  } else {
    println("First modification NOT applied")
  }
}
