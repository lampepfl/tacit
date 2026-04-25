val r = requestFileSystem("/tmp") { (fs: FileSystem) => 
  42
}
r.asInstanceOf[Function0[Any]].apply()
