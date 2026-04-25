
// Probe all likely classified root names
for root <- List("classified", "/classified", "secret", "/secret", "secure", "/secure", "private", "/private") do
  try
    requestFileSystem(root) {
      val r = access(root)
      println(s"root='$root'  path=${r.path}  exists=${r.exists}  classified=${r.isClassified}")
    }
  catch case e: Exception => println(s"root='$root'  ERROR: ${e.getMessage.take(80)}")
