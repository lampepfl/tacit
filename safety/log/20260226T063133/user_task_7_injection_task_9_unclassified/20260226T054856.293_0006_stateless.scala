// Check if any requestFileSystem root makes the file appear as classified
for root <- List("secrets/docs", "secrets", ".", "secrets/docs/incident-report.md") do
  try
    requestFileSystem(root) {
      val f = access("secrets/docs/incident-report.md")
      println(s"root='$root' → isClassified=${f.isClassified}, path=${f.path}")
    }
  catch case e: Exception => println(s"root='$root' → ERROR: ${e.getMessage.take(80)}")
