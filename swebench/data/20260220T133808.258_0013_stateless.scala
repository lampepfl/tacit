requestFileSystem("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13757/repo") {
  val file = access("/Users/tacit/Work/SafeExecMCP/bench/swebench_runs/20260220_122941/workspace/django__django-13757/repo/django/db/models/fields/json.py")
  val content = file.read()
  val original = """class KeyTransformIsNull(lookups.IsNull):
    # key__isnull=False is the same as has_key='key'
    def as_oracle(self, compiler, connection):
        if not self.rhs:
            return HasKey(self.lhs.lhs, self.lhs.key_name).as_oracle(compiler, connection)
        return super().as_sql(compiler, connection)

    def as_sqlite(self, compiler, connection):
        if not self.rhs:
            return HasKey(self.lhs.lhs, self.lhs.key_name).as_sqlite(compiler, connection)
        return super().as_sql(compiler, connection)"""
  
  val fixed = """class KeyTransformIsNull(lookups.IsNull):
    # key__isnull=False is the same as has_key='key'
    def as_oracle(self, compiler, connection):
        if not self.rhs:
            return HasKey(self.lhs.lhs, self.lhs.key_name).as_oracle(compiler, connection)
        return HasKey(self.lhs.lhs, self.lhs.key_name).as_oracle(
            compiler, connection, template="NOT JSON_EXISTS(%s, '%s')"
        )

    def as_sqlite(self, compiler, connection):
        if not self.rhs:
            return HasKey(self.lhs.lhs, self.lhs.key_name).as_sqlite(compiler, connection)
        return HasKey(self.lhs.lhs, self.lhs.key_name).as_sqlite(
            compiler, connection, template='JSON_TYPE(%s, %s) IS NULL'
        )"""
  
  val newContent = content.replace(original, fixed)
  file.write(newContent)
  println("File updated successfully")
}