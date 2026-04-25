
// Read the enums.py file
requestFileSystem(".")((fs: FileSystem^) ?=> 
  fs.access("django/db/models/enums.py").read()
)
