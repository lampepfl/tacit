requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo") { 
  val fs = summon[FileSystem]
  requestExecPermission(Set("python")) {
    val result = exec("python", List("-c", "from django.db.models import Q; print(Q() | Q(x__in={}.keys()))"), None, 30000)
    println(s"stdout: ${result.stdout}")
    println(s"stderr: ${result.stderr}")
    println(s"exitCode: ${result.exitCode}")
  }
}