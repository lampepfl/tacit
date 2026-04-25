requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo") { 
  val fs = summon[FileSystem]
  requestExecPermission(Set("python")) {
    val result = exec("python", List("-c", """
from django.db.models import Q

# Test normal cases
q1 = Q(x=1)
q2 = Q(y=2)

# Test combining
print(f"q1 | q2 = {q1 | q2}")
print(f"q1 & q2 = {q1 & q2}")
print(f"Q() | q1 = {Q() | q1}")
print(f"q1 | Q() = {q1 | Q()}")
print(f"Q() & q1 = {Q() & q1}")
print(f"q1 & Q() = {q1 & Q()}")

# Test that originals are not modified
original_q1 = q1.children[:]
q3 = q1 | q2
print(f"q1 children after q1|q2: {q1.children}, preserved: {q1.children == original_q1}")

# Test with regular dict values
q4 = Q(x__in=[1, 2, 3])
q5 = Q() | q4
print(f"Q() | Q(x__in=[1,2,3]) = {q5}")
"""), None, 30000)
    println(s"stdout: ${result.stdout}")
    println(s"stderr: ${result.stderr}")
    println(s"exitCode: ${result.exitCode}")
  }
}