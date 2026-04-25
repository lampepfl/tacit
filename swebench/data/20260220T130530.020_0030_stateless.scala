{ // Use a block
  requestFileSystem("/tmp") { (fs: FileSystem) => 
    val exists = fs.access("/tmp").exists
    println(s"tmp exists: $exists")
    exists
  }
}
