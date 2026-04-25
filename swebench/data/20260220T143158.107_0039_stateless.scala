requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo") { 
  val autoreload = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-11422/repo/django/utils/autoreload.py")
  val content = autoreload.read()
  // Remove the duplicate lines: 
  // "        self.extra_files = set()"
  // "        self.directory_globs = defaultdict(set)"
  // "        self._stop_condition = threading.Event()"
  val bad = """                  self.extra_files.add(script_path)
        self.extra_files = set()
        self.directory_globs = defaultdict(set)
        self._stop_condition = threading.Event()

    
def watch_dir"""
  val good = """                  self.extra_files.add(script_path)

    
def watch_dir"""
  val newContent = content.replace(bad, good)
  autoreload.write(newContent)
}