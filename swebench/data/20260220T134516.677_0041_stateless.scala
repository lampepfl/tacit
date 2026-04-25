import library.*

given ProcessPermission = new ProcessPermission(Set("grep", "cat", "find"), false)
exec("grep", List("-r", "only.*defer\\|defer.*only", "./tests", "--include=*.py")).stdout.take(500)