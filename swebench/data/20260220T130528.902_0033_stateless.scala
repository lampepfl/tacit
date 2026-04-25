
// Try calling requestFileSystem as a method with parentheses
def processFs(fs: FileSystem): String = {
  fs.access(".").children.map(x => x.name).mkString(", ")
}

val r = requestFileSystem(".").apply(processFs)
r
