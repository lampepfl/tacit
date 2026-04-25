
// Search for get_db_prep_value or related methods that handle converting value for database
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grep("django/db/models/fields/__init__.py", "def get_db_prep_value").take(10).mkString("\n")
)
