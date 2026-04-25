
val x = requestFileSystem(".")((fs: FileSystem^) ?=> grepRecursive("tests/model_enums", "isinstance", "*.py").take(10).mkString("\n"))
x
