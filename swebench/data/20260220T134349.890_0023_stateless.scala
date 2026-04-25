import library.*

val result = requestFileSystem(".")((fs: FileSystem) => "hello")
println(result.toString)