requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo") {
  requestExecPermission(Set("python3")) {
    given perm: ProcessPermission = summon[ProcessPermission]
    given fs: FileSystem = summon[FileSystem]
    
    val result = exec("python3", List("-m", "py_compile", 
      "/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo/django/contrib/admin/utils.py"
    ), Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12308/repo"), 30000)
    
    println(s"Exit code: ${result.exitCode}")
    if result.exitCode == 0 then println("Syntax OK") else println(s"Error: ${result.stderr}")
  }
}
