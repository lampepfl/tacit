requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo") { 
  val fs = summon[FileSystem]
  requestExecPermission(Set("python")) {
    val result = exec("python", List("-c", """
from django.db.models import Q

# Test when other is empty
self = Q(x__in={}.keys())
other = Q()

# Simulate the fix for both branches
# Branch 1: if not other: return copy.deepcopy(self)
if not other:
    obj = type(self)()
    obj.connector = self.connector
    obj.negated = self.negated
    obj.children = self.children[:]
print(f"Test 1 - other empty: {obj}")

# Branch 2: elif not self: return copy.deepcopy(other)
self2 = Q()
other2 = Q(x__in={}.keys())
if not other2:
    obj2 = type(self2)()
    obj2.connector = self2.connector
    obj2.negated = self2.negated
    obj2.children = self2.children[:]
elif not self2:
    obj2 = type(other2)()
    obj2.connector = other2.connector
    obj2.negated = other2.negated
    obj2.children = other2.children[:]
print(f"Test 2 - self empty: {obj2}")
"""), None, 30000)
    println(s"stdout: ${result.stdout}")
    println(s"stderr: ${result.stderr}")
    println(s"exitCode: ${result.exitCode}")
  }
}