val x = requestFileSystem("/tmp") { (fs: FileSystem) => fs }
println(s"Type of x: ${x.getClass.getName}")
x