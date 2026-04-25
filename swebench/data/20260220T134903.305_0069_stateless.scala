import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat", "sed", "cp"), false)

// Restore from backup
exec("cp", List("./django/db/models/sql/query.py.bak", "./django/db/models/sql/query.py"))

// Verify it's restored
exec("sed", List("-n", "2087,2090p", "./django/db/models/sql/query.py"))