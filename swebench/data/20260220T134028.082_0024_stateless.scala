val repoPath = "/tmp"

requestFileSystem(repoPath) { (fs: FileSystem) =>
  given FileSystem = fs
  val f = access("/tmp/test.txt")
  if f.exists then f.read() else "file not found"
}