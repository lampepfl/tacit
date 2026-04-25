// First let's understand the directory structure better
import scala.util.Try

// List the current working directory 
requestFileSystem(".") {
  val root = access(".")
  println(s"Root path: ${root.path}")
  println(s"Root name: ${root.name}")
  println(s"Is directory: ${root.isDirectory}")
}
