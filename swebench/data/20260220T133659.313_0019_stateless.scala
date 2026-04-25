requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo") { 
  val fs = summon[FileSystem]
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo/django/db/models/query_utils.py")
  val content = file.read()
  val lines = content.split("\n")
  
  // Modify lines 47-52
  val newLines = lines.zipWithIndex.map { case (line, idx) =>
    val lineNum = idx + 1
    if lineNum == 47 then "# If the other Q() is empty, ignore it and just use a copy of `self`."
    else if lineNum == 48 then "        if not other:"
    else if lineNum == 49 then "            obj = type(self)()\n            obj.connector = self.connector\n            obj.negated = self.negated\n            obj.children = self.children[:]\n            return obj"
    else if lineNum == 50 then "# Or if this Q is empty, ignore it and just use a copy of `other`."
    else if lineNum == 51 then "        elif not self:"
    else if lineNum == 52 then "            obj = type(other)()\n            obj.connector = other.connector\n            obj.negated = other.negated\n            obj.children = other.children[:]\n            return obj"
    else line
  }.flatMap { line =>
    if line.contains("\n") then line.split("\n").toList else List(line)
  }
  
  file.write(newLines.mkString("\n"))
  println("File updated successfully")
}