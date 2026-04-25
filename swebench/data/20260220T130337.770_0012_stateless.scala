
inline def testFs(): String = summonFrom {
  case fs: FileSystem => find(".", "*").mkString(", ")
}

testFs()
