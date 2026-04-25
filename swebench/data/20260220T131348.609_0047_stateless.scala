val result = requestFileSystem("/tmp")((fs: FileSystem) => { val x = fs.access("/tmp").exists; println(s"exists: $x"); x }); println(s"Result: $result")
