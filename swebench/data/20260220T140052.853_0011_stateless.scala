val result = requestFileSystem(".")((fs: FileSystem) => access(".").name)
println(result)