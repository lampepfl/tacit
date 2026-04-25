// Let's try using the function apply method
val req = requestFileSystem _
req("/tmp").apply((fs: FileSystem) => fs.access("/tmp").exists)
