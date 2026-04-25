
// Let's check if there are any tests related to TextChoices/IntegerChoices
requestFileSystem("."){ (fs: FileSystem^) => 
  grepRecursive("tests", "TextChoices", "*.py").take(10).mkString("\n")
}
