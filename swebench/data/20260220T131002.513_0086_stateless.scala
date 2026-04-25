
// Let's look at how values are loaded from the database
// Search for the code that handles converting database values to Python values
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grepRecursive("django/db/models", "from_db_value", "*.py").take(10).mkString("\n")
)
