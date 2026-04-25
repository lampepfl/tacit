
requestFileSystem("."){ (fs: FileSystem^) => 
  fs.access("django/db/models/fields/__init__.py").readLines().take(5).mkString("\n")
}
