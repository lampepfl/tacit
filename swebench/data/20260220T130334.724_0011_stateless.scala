
import scala.compiletime.summonFrom

def testFs(): String = summonFrom {
  case fs: FileSystem => find(".", "*").mkString(", ")
}

testFs()
