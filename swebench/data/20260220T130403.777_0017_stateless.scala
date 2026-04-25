
// Try to work around the capture checking issue by using a local method
def runWithFileSystem[T](root: String)(op: FileSystem => T): T = {
  requestFileSystem(root)(fs => op(fs))
}

runWithFileSystem(".")(fs => access(".").children.map(f => f.name).mkString(", "))
