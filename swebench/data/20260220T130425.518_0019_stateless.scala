// Simple test - just use one-liner to call the function
val testResult = requestFileSystem("/tmp") { (fs: FileSystem) => 
  fs.access("/tmp").exists
}
testResult
