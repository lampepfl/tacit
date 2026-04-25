requestExecPermission(Set("python3")) {
  exec("python3", List("-c", """
import functools
import sys
sys.path.insert(0, '/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-14155/repo')

# Import directly without Django setup
from django.urls.resolvers import ResolverMatch

def my_view(request, id):
    pass

# Test with a regular function
match = ResolverMatch(my_view, (1,), {'kwarg1': 'val1'}, url_name='test')
print('Regular function:', repr(match))

# Test with functools.partial
partial_view = functools.partial(my_view, default_arg='default')
match2 = ResolverMatch(partial_view, (1,), {'kwarg1': 'val1'}, url_name='test')
print('Partial function:', repr(match2))

# Test with nested partial
partial_view2 = functools.partial(partial_view, another_arg='value')
match3 = ResolverMatch(partial_view2, (1,), {'kwarg1': 'val1'}, url_name='test')
print('Nested partial:', repr(match3))
"""))
}