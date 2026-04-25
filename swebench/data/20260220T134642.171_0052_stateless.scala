import library.*

given ProcessPermission = new ProcessPermission(Set("grep", "cat"), false)
exec("grep", List("-n", "deferred_loading", "./django/db/models/sql/query.py")).stdout.take(1000)