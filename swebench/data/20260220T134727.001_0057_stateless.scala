import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat", "sed", "cp"), false)

val content = exec("cat", List("./django/db/models/sql/query.py")).stdout
val lines = content.split("\n")
lines(2088) = "            new_fields = existing.difference(field_names)\n            self.deferred_loading = (new_fields, new_fields is existing)"
val newContent = lines.mkString("\n")
exec("cp", List("./django/db/models/sql/query.py", "./django/db/models/sql/query.py.fix2"))
exec("python3", List("-c", s"open('./django/db/models/sql/query.py', 'w').write('''$newContent''')"))