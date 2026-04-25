
val x = requestFileSystem(".")((fs: FileSystem^) ?=> grepRecursive("tests", "TextChoices", "*.py").take(10).mkString("\n"))
x
