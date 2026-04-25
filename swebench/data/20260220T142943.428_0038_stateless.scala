requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo") { 
  val testDir = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo")
  val testFile = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-12284/repo/test_choices.py")
  
  val testCode = """
import os
import django
from django.conf import settings

if not settings.configured:
    settings.configure(
        DEBUG=True,
        DATABASES={
            'default': {
                'ENGINE': 'django.db.backends.sqlite3',
                'NAME': ':memory:',
            }
        },
        INSTALLED_APPS=[
            'django.contrib.contenttypes',
            'django.contrib.auth',
        ],
    )
    django.setup()

from django.db import models

class A(models.Model):
    foo_choice = [("A","output1"),("B","output2")]
    field_foo = models.CharField(max_length=254, choices=foo_choice)
    
    class Meta:
        abstract = True

class B(A):
    foo_choice = [("A","output1"),("B","output2"),("C","output3")]
    field_foo = models.CharField(max_length=254, choices=foo_choice)
    
    class Meta:
        app_label = 'test'

# Test
b = B(field_foo='C')
print(f"Value: {b.field_foo}")
print(f"Display: {b.get_field_foo_display()}")
print(f"Expected: output3")
print(f"Test passed: {b.get_field_foo_display() == 'output3'}")

# Test A choices still work
b2 = B(field_foo='A')
print(f"Display for A: {b2.get_field_foo_display()}")
print(f"Expected: output1")
print(f"Test passed: {b2.get_field_foo_display() == 'output1'}")

b3 = B(field_foo='B')
print(f"Display for B: {b3.get_field_foo_display()}")
print(f"Expected: output2")
print(f"Test passed: {b3.get_field_foo_display() == 'output2'}")
"""
  
  testFile.write(testCode)
  println("Test file created")
}