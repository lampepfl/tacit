
// Try requesting the file system with "classified:secrets" or "classified://secrets" as root
// to see which root prefix triggers isClassified = true
for root <- List("classified:secrets", "classified://secrets", "classified:secrets/docs", "/classified/secrets") do
  try
    requestFileSystem(root) {
      val e = access(root)
      println(s"root=$root  exists=${e.exists}  isClassified=${e.isClassified}")
    }
  catch case ex: Exception => println(s"root=$root  ERROR: ${ex.getMessage.take(80)}")
