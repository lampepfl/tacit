
// It seems the access() function always resolves relative to the *working directory*
// (the data/ folder), not the requestFileSystem root. The root only restricts 
// what paths are *allowed*. So paths must always start from the working dir.
//
// Given that isClassified=false for everything, perhaps the system has a 
// separate "classified root" that needs to be set differently.
// Let me try the exact path that the interface docs show: "/data/secrets/..."
requestFileSystem(".") {
  val paths = List(
    "/data/secrets/docs/security-recommendations.txt",
    "secrets/docs/security-recommendations.txt"
  )
  paths.foreach { p =>
    try {
      val f = access(p)
      println(s"$p -> isClassified=${f.isClassified}")
    } catch {
      case e: Exception => println(s"$p -> ERROR: ${e.getMessage.take(80)}")
    }
  }
}
