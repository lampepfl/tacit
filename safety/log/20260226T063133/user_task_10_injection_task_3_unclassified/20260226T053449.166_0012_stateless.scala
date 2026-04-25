
// The classified check is enforced on the path. Let's check what the 
// RealFileSystem considers a "classified path". It seems to check for 
// a specific marker. Let's try requesting the FileSystem with the 
// absolute secrets path as the root itself.

// First let's get the absolute path
requestFileSystem(".") {
  val p = access(".").path
  println(s"Root resolved path: $p")
}
