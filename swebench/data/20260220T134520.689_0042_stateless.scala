import library.*

given ProcessPermission = new ProcessPermission(Set("python3", "cat"), false)
exec("python3", List("-m", "django", "test", "defer.tests", "--settings=tests.test_sqlite", "-v", "2"), Some("./tests"))