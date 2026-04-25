requestFileSystem("/tmp") { (fs: FileSystem) => 
  println("INNER CALLED")
  fs.access("/tmp").exists
}
