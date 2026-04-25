
// Do everything in one block
def processFs(fs: FileSystem): String = {
  fs.access(".").children.map(x => x.name).mkString(", ")
}

val r = requestFileSystem(".")(processFs)
r
