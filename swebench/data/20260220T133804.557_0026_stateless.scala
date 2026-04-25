requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo") { 
  val fs = summon[FileSystem]
  requestExecPermission(Set("python")) {
    val result = exec("python", List("-c", """
from django.db.models import Q

# Original test case from the bug report
print("Test 1: Q(x__in={}.keys())")
print(Q(x__in={}.keys()))

print("\nTest 2: Q() | Q(x__in={}.keys())")
print(Q() | Q(x__in={}.keys()))

print("\nTest 3: Q(x__in={}.keys()) | Q()")
print(Q(x__in={}.keys()) | Q())

print("\nTest 4: Q() & Q(x__in={}.keys())")
print(Q() & Q(x__in={}.keys()))

print("\nTest 5: Q(x__in={}.keys()) & Q()")
print(Q(x__in={}.keys()) & Q())

print("\nAll tests passed!")
"""), None, 30000)
    println(s"stdout: ${result.stdout}")
    println(s"stderr: ${result.stderr}")
    println(s"exitCode: ${result.exitCode}")
  }
}