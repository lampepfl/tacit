val f: FileSystem => Unit = (fs: FileSystem) => println("test")
requestFileSystem(".")(f)