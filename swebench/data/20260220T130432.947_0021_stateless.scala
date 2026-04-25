val r = requestFileSystem("/tmp") { (fs: FileSystem) => 
  42
}
println(s"r class: ${r.getClass}")
