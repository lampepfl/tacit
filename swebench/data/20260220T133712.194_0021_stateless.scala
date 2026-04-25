requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo") { 
  val fs = summon[FileSystem]
  requestExecPermission(Set("python")) {
    val result = exec("python", List("-c", """
from django.db.models import Q

# Test all cases
q1 = Q(x__in={}.keys())
original_children = q1.children[:]

# Case 1: Q() | q1
result1 = Q() | q1
print(f"Q() | q1 = {result1}")

# Case 2: q1 | Q()
result2 = q1 | Q()
print(f"q1 | Q() = {result2}")

# Case 3: Q() & q1
result3 = Q() & q1
print(f"Q() & q1 = {result3}")

# Case 4: q1 & Q()
result4 = q1 & Q()
print(f"q1 & Q() = {result4}")

# Verify original is not modified
print(f"Original preserved: {q1.children == original_children}")
"""), None, 30000)
    println(s"stdout: ${result.stdout}")
    println(s"stderr: ${result.stderr}")
    println(s"exitCode: ${result.exitCode}")
  }
}