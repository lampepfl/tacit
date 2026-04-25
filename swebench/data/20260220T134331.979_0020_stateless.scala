import library.*

requestFileSystem(".")((fs: FileSystem) => 
  grep(".", "defer")
)