import scala.util.Try
val tryResult = Try(requestFileSystem("/tmp")((fs: FileSystem) => {println("INNER"); fs.access("/tmp").exists} ))
println(s"Try result: $tryResult")
