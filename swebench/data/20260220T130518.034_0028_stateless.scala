// Let me try with implicit conversion
import scala.implicits.*
requestFileSystem("/tmp") {
  access("/tmp").exists
}
