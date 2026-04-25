
// Use the access method on FileSystem instead of the global function
// This might work better with the capture checking

requestFileSystem(".")(fs => 
  fs.access(".").children.map(x => x.name).mkString(", ")
)
