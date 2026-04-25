requestExecPermission(Set("python3", "cat")) {
  exec("python3", List("-c", """
import functools

def my_view(request, id):
    pass

p = functools.partial(my_view, 1)

print('has __name__:', hasattr(p, '__name__'))
print('__name__:', getattr(p, '__name__', 'N/A'))
print('__class__.__name__:', p.__class__.__name__)
print('__class__.__module__:', p.__class__.__module__)
print('func:', p.func)
print('func.__name__:', p.func.__name__)
print('func.__module__:', p.func.__module__)
print('keywords:', p.keywords)
print('args:', p.args)
"""))
}