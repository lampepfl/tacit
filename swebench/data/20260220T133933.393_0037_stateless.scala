requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14016/repo") { 
  val fs = summon[FileSystem]
  requestExecPermission(Set("python")) {
    val result = exec("python", List("-c", """
import sys
sys.path.insert(0, 'tests')
import django
from django.conf import settings
settings.configure(
    DEBUG=True,
    DATABASES={'default': {'ENGINE': 'django.db.backends.sqlite3', 'NAME': ':memory:'}},
    INSTALLED_APPS=['django.contrib.contenttypes', 'django.contrib.auth'],
    USE_TZ=True,
)
django.setup()

from django.db.models import Q

print("=== Testing Q with non-pickleable objects ===")

q1 = Q(x__in={}.keys())
print(f"Q(x__in={{}}.keys()) = {q1}")

q2 = Q(x__in={}.values())
print(f"Q(x__in={{}}.values()) = {q2}")

print("\n=== Testing combinations with empty Q ===")
print(f"Q() | Q(x__in={{}}.keys()) = {Q() | Q(x__in={}.keys())}")
print(f"Q(x__in={{}}.keys()) | Q() = {Q(x__in={}.keys()) | Q()}")
print(f"Q() & Q(x__in={{}}.keys()) = {Q() & Q(x__in={}.keys())}")
print(f"Q(x__in={{}}.keys()) & Q() = {Q(x__in={}.keys()) & Q()}")

print("\n=== Testing regular Q combinations ===")
q3 = Q(x=1)
q4 = Q(y=2)
print(f"Q(x=1) | Q(y=2) = {q3 | q4}")
print(f"Q(x=1) & Q(y=2) = {q3 & q4}")

print("\n=== Testing originals are not modified ===")
original_q1_children = q1.children[:]
_ = Q() | q1
print(f"q1.children after Q() | q1: {q1.children}, preserved: {q1.children == original_q1_children}")

original_q3_children = q3.children[:]
_ = q3 | Q()
print(f"q3.children after q3 | Q(): {q3.children}, preserved: {q3.children == original_q3_children}")

print("\n=== Testing negation ===")
q5 = ~Q(x__in={}.keys())
print(f"~Q(x__in={{}}.keys()) = {q5}")

print("\n=== All tests passed! ===")
"""), None, 60000)
    println(s"stdout: ${result.stdout}")
    println(s"stderr: ${result.stderr}")
    println(s"exitCode: ${result.exitCode}")
  }
}