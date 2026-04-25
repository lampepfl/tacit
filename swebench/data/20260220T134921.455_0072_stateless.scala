import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat", "head"), false)
exec("head", List("-n", "2095", "./django/db/models/sql/query.py")).stdout.split("\n").last(5)