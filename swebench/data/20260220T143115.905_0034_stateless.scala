requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo") { 
  val autoreload = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo/django/utils/autoreload.py")
  val content = autoreload.read()
  // Find and remove the duplicate section
  val parts = content.split("class BaseReloader:")
  val beforeBase = parts(0)
  val afterBase = parts(1)
  // Get the proper content after BaseReloader
  val afterInit = afterBase.split("def watch_dir(self, path, glob):")
  val initBlock = afterInit(0)
  // Clean up the init block - find where the real __init__ ends
  val lines = initBlock.split("\n").toList
  // Find the line after the second "self._stop_condition = threading.Event()"
  var foundSecond = false
  var removeFrom = -1
  lines.zipWithIndex.foreach { (line, idx) =>
    if line.trim == "self._stop_condition = threading.Event()" then
      if foundSecond then
        removeFrom = idx
      else
        foundSecond = true
  }
  val newInitBlock = if removeFrom > 0 then lines.take(removeFrom).mkString("\n") else initBlock
  val newContent = beforeBase + "class BaseReloader:" + newInitBlock + "def watch_dir(self, path, glob):" + afterInit(1)
  autoreload.write(newContent)
}