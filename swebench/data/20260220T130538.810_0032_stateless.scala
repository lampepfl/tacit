requestFileSystem("/tmp") { (fs: FileSystem) => println(fs.access("/tmp").name) }
