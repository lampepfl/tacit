
// Search for TextChoices using grep
requestFileSystem(".")((fs: FileSystem^) ?=> 
  grep("django", "class TextChoices").mkString("\n")
)
