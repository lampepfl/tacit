import library.*

given ProcessPermission = new ProcessPermission(Set("sed", "cat", "python3", "cp"), false)

val content = exec("cat", List("./django/db/models/sql/query.py")).stdout
val lines = content.split("\n")
lines(2088) = "            self.deferred_loading = existing.difference(field_names), True"
val newContent = lines.mkString("\n")
exec("cp", List("./django/db/models/sql/query.py", "./django/db/models/sql/query.py.bak"))
exec("python3", List("-c", s"open('./django/db/models/sql/query.py', 'w').write('''$newContent''')"))