requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13757/repo") {
  requestExecPermission(Set("python3")) {
    val result = execOutput("python3", List("-c", """
import re

with open('django/db/models/fields/json.py', 'r') as f:
    content = f.read()

# Find and replace the KeyTransformIsNull class
old_code = '''class KeyTransformIsNull(lookups.IsNull):
    # key__isnull=False is the same as has_key='key'
    def as_oracle(self, compiler, connection):
        if not self.rhs:
            return HasKey(self.lhs.lhs, self.lhs.key_name).as_oracle(compiler, connection)
        return super().as_sql(compiler, connection)

    def as_sqlite(self, compiler, connection):
        if not self.rhs:
            return HasKey(self.lhs.lhs, self.lhs.key_name).as_sqlite(compiler, connection)
        return super().as_sql(compiler, connection)'''

new_code = '''class KeyTransformIsNull(lookups.IsNull):
    # key__isnull=False is the same as has_key='key'
    def as_oracle(self, compiler, connection):
        if not self.rhs:
            return HasKey(self.lhs.lhs, self.lhs.key_name).as_oracle(compiler, connection)
        return HasKey(self.lhs.lhs, self.lhs.key_name).as_oracle(
            compiler, connection, template=\"NOT JSON_EXISTS(%s, '%s')\"
        )

    def as_sqlite(self, compiler, connection):
        if not self.rhs:
            return HasKey(self.lhs.lhs, self.lhs.key_name).as_sqlite(compiler, connection)
        return HasKey(self.lhs.lhs, self.lhs.key_name).as_sqlite(
            compiler, connection, template='JSON_TYPE(%s, %s) IS NULL'
        )'''

content = content.replace(old_code, new_code)

with open('django/db/models/fields/json.py', 'w') as f:
    f.write(content)

print('Done')
"""), workingDir = Some("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13757/repo"))
    println(result)
  }
}