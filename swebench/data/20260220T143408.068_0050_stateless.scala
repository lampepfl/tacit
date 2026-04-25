requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo") { 
  val autoreload = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo/django/utils/autoreload.py")
  val content = autoreload.read()
  val before = "class BaseReloader:\n    def __init__(self):"
  val after = "\n    def watch_dir(self, path, glob):"
  val startIdx = content.indexOf(before)
  val endIdx = content.indexOf(after)
  val beforePart = content.slice(0, startIdx + before.length)
  val afterPart = content.slice(endIdx, content.length)
  
  // Now build the new __init__ with correct indentation
  val newInit = """
    def __init__(self):
        self.extra_files = set()
        self.directory_globs = defaultdict(set)
        self._stop_condition = threading.Event()
        # Add the main script file (e.g., manage.py) to extra files to watch.
        # This ensures changes to the main script are detected when running
        # with `python manage.py runserver` (as opposed to `python -m django`).
        if sys.argv[0]:
            script_path = pathlib.Path(sys.argv[0])
            if script_path.exists():
                self.extra_files.add(script_path)
"""
  
  autoreload.write(beforePart + newInit + afterPart)
}