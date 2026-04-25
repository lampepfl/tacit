// Try a workaround - using val assignment at top level
val fsOp: FileSystem => Boolean = (fs) => fs.access("/tmp").exists
val result = requestFileSystem("/tmp")(fsOp)
result
