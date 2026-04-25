requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo") { 
  val fs = summon[FileSystem]
  requestExecPermission(Set("python")) {
    val result = exec("python", List("-c", """
from django.db.models import Q

# Test the fix - create new Q and copy properties manually
other = Q(x__in={}.keys())
print(f"other: {other}")
print(f"other.children: {other.children}")

obj = type(other)()
obj.connector = other.connector
obj.negated = other.negated
obj.children = other.children[:]  # shallow copy

print(f"obj: {obj}")
print(f"obj.children: {obj.children}")

# Test that original is not modified
obj.children.append('test')
print(f"other.children after mod: {other.children}")
print(f"obj.children: {obj.children}")
"""), None, 30000)
    println(s"stdout: ${result.stdout}")
    println(s"stderr: ${result.stderr}")
    println(s"exitCode: ${result.exitCode}")
  }
}