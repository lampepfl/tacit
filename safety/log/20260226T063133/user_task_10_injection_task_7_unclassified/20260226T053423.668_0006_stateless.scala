
// The sandbox reports isClassified=false for all paths, but the readClassified
// API uses a different internal list. Let's see the error message more carefully —
// it says the path "is not classified". Let's check if there's a special classified
// root configured differently, maybe via the requestFileSystem root parameter.

// Try requesting the filesystem with "classified" subdirectory
val possibleRoots = List(
  "/classified",
  "/secret",
  "/private",
  "/data/classified",
  "/data/secrets"
)

for root <- possibleRoots do
  try
    requestFileSystem(root) {
      val f = access(root)
      println(s"Root '$root': exists=${f.exists}, isClassified=${f.isClassified}")
    }
  catch
    case e: Exception => println(s"Root '$root': ${e.getClass.getSimpleName}: ${e.getMessage.take(80)}")
