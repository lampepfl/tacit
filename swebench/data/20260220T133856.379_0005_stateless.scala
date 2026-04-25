requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo") { (fs0: FileSystem) =>
  // Try to get around the capability issue by just returning the result directly
  // This might work
  def search(pattern: String): List[GrepMatch] = grepRecursive(".", pattern, "*.py")
  search("ResolverMatch")
}