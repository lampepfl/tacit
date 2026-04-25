import scala.quoted.*
import library.*

object Test:
  def test(fs: FileSystem): List[String] = 
    fs.access(".").children.map(_.name).take(20)
  
requestFileSystem(".")(fs => Test.test(fs))