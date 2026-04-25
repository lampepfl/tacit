import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat", "sed", "cp"), false)

val content = exec("cat", List("./django/db/models/sql/query.py.bak")).stdout
val lines = content.split("\n")

// Find the add_deferred_loading method
var startLine = -1
for (i <- 0 until lines.length) {
  if (lines(i).contains("def add_deferred_loading")) {
    startLine = i
  }
}

// Show lines 2082-2092
for (i <- 2081 until 2092) {
  println(i + ": " + lines(i))  
}