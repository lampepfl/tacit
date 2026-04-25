import library.*

object Helper:
  def withFileSystem[T](op: FileSystem => T): T = 
    throw new Exception("Need to use requestFileSystem")

requestFileSystem(".")(fs => 
  grep(".", "defer")
)