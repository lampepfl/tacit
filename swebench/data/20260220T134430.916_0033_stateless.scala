import library.*

given ProcessPermission = new ProcessPermission(Set("grep", "cat"), false)
exec("grep", List("-n", "def add_deferred_loading\\|def add_immediate_loading\\|def clear_deferred_loading", "./django/db/models/sql/query.py"))