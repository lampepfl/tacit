import library.*

given ProcessPermission = new ProcessPermission(Set("sed", "cat", "python3"), false)

val content = exec("cat", List("./django/db/models/sql/query.py")).stdout
val lines = content.split("\n")
println(lines(2088)) // 0-indexed, so line 2089 is index 2088