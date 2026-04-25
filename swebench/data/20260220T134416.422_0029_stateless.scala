import library.*

given ProcessPermission = new ProcessPermission(Set("grep", "cat"), false)
grep(".", "class QuerySet")