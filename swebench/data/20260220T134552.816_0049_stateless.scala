import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat"), false)

val testScript = """
import sys
sys.path.insert(0, '.')
import os
os.environ['DJANGO_SETTINGS_MODULE'] = 'tests.test_sqlite'

# Setup Django
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

from django.db import models, connection

# Create a simple model
class Company(models.Model):
    name = models.CharField(max_length=100)
    trade_number = models.CharField(max_length=100)
    country = models.CharField(max_length=100)
    
    class Meta:
        app_label = 'test'

# Create the table
with connection.schema_editor() as schema_editor:
    schema_editor.create_model(Company)

# Test 1: only("name").defer("name") - should defer all fields except pk
qs = Company.objects.only("name").defer("name")
query = str(qs.query)
print("Test 1: only('name').defer('name')")
print(f"Query: {query}")

# Check if the query only selects id
if 'id' in query and 'name' not in query.split('FROM')[0] and 'trade_number' not in query.split('FROM')[0]:
    print("PASS: Only id is selected")
else:
    print("FAIL: More fields than expected are selected")

# Test 2: only("name").defer("name").defer("country") - should defer all fields except pk
qs2 = Company.objects.only("name").defer("name").defer("country")
query2 = str(qs2.query)
print("\\nTest 2: only('name').defer('name').defer('country')")
print(f"Query: {query2}")

if 'id' in query2 and 'name' not in query2.split('FROM')[0] and 'trade_number' not in query2.split('FROM')[0] and 'country' not in query2.split('FROM')[0]:
    print("PASS: Only id is selected")
else:
    print("FAIL: More fields than expected are selected")

# Test 3: only("name", "country").defer("name") - should only load id and country (this already works)
qs3 = Company.objects.only("name", "country").defer("name")
query3 = str(qs3.query)
print("\\nTest 3: only('name', 'country').defer('name')")
print(f"Query: {query3}")

# Check that only id and country are selected
select_part = query3.split('FROM')[0]
if 'id' in select_part and 'country' in select_part and 'name' not in select_part and 'trade_number' not in select_part:
    print("PASS: Only id and country are selected")
else:
    print("FAIL: Incorrect fields selected")
"""

exec("python3", List("-c", testScript))