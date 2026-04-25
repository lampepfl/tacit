
// It seems `isClassified` is always false in this environment, yet the task says
// to use writeClassified. Let's inspect what the classified prefix is configured to
// by looking at the source — or try a path that literally starts with "/classified" or "classified/"
val testPaths = List(
  "classified/test.txt",
  "data/secrets/test.txt",
  "private/test.txt"
)
requestFileSystem(".") {
  for p <- testPaths do
    try
      val f = access(p)
      println(s"path=$p -> absPath=${f.path} isClassified=${f.isClassified}")
    catch case e: Exception => println(s"path=$p ERROR: ${e.getMessage.take(80)}")
}
