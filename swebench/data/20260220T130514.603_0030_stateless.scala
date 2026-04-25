
// Let's try defining and calling in the same block
def processFs2(fs: FileSystem): String = {
  fs.access(".").children.map(x => x.name).mkString(", ")
}

requestFileSystem(".")(processFs2)
