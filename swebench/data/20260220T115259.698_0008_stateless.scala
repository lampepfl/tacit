import library.*

requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11049/repo") { (fs: FileSystem) ?=>
  given FileSystem = fs
  val f = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11049/repo/django/db/models/fields/__init__.py")
  val lines = f.readLines()
  val lineToChange = lines(1588)
  println(s"Original line 1588: $lineToChange")
  
  val newLine = lineToChange.replace("[HH:[MM:]]", "[[HH:]MM:]")
  println(s"New line 1588: $newLine")
  
  val newLines = lines.updated(1588, newLine)
  f.write(newLines.mkString("\n"))
  println("File updated")
}