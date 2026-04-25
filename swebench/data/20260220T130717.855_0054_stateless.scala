
// Use grepRecursive to search recursively
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grepRecursive("django", "class TextChoices", "*.py").take(10).mkString("\n")
)
