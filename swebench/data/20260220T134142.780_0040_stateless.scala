object FileChecker:
  def writeTest(): Boolean =
    var success = false
    requestFileSystem("/tmp") { (fs: FileSystem) =>
      given FileSystem = fs
      val f = access("/tmp/test_file_check.txt")
      f.write("test content")
      success = true
    }
    success

FileChecker.writeTest()