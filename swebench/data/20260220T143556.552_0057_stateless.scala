requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo") { 
  val autoreload = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo/django/utils/autoreload.py")
  val content = autoreload.read()
  
  // Find the broken __init__ section and fix it
  // The broken section starts with "class BaseReloader:" and has duplicate def __init__
  val lines = content.split("\n").toList
  var newLines: List[String] = Nil
  var i = 0
  var foundClass = false
  var foundInit = false
  var inBrokenInit = false
  
  while i < lines.length do
    val line = lines(i)
    if !foundClass && line == "class BaseReloader:" then
      foundClass = true
      newLines = newLines :+ line
      i += 1
    else if foundClass && !foundInit && line.contains("def __init__(self):") then
      foundInit = true
      inBrokenInit = true
      // Replace with the correct __init__
      newLines = newLines :+ "    def __init__(self):"
      newLines = newLines :+ "        self.extra_files = set()"
      newLines = newLines :+ "        self.directory_globs = defaultdict(set)"
      newLines = newLines :+ "        self._stop_condition = threading.Event()"
      newLines = newLines :+ "        # Add the main script file (e.g., manage.py) to extra files to watch."
      newLines = newLines :+ "        # This ensures changes to the main script are detected when running"
      newLines = newLines :+ "        # with `python manage.py runserver` (as opposed to `python -m django`)."
      newLines = newLines :+ "        if sys.argv[0]:"
      newLines = newLines :+ "            script_path = pathlib.Path(sys.argv[0])"
      newLines = newLines :+ "            if script_path.exists():"
      newLines = newLines :+ "                self.extra_files.add(script_path)"
      // Skip lines until we hit the next method
      while i < lines.length && !lines(i).startsWith("    def watch_dir") do
        i += 1
    else if inBrokenInit && line.startsWith("    def watch_dir") then
      inBrokenInit = false
      newLines = newLines :+ line
      i += 1
    else if !inBrokenInit then
      newLines = newLines :+ line
      i += 1
    else
      i += 1
  
  autoreload.write(newLines.mkString("\n"))
}