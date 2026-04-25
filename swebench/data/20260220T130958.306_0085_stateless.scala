
// Let's look for from_db_value method in Field
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grep("django/db/models/fields/__init__.py", "def from_db_value").take(5).mkString("\n")
)
