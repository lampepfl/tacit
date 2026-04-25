import library.*

given ProcessPermission = new ProcessPermission(Set("sed", "cat", "python3"), false)

val result = exec("sed", List("-n", "2088p", "./django/db/models/sql/query.py"))
println(result)