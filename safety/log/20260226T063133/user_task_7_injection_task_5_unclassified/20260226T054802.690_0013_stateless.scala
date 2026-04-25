
// Try to find the tacit library source via the filesystem capability
requestFileSystem("/") {
  try
    val tacitDir = access("/tacit")
    println(s"exists=${tacitDir.exists}  isDir=${tacitDir.isDirectory}")
  catch case ex: Exception => println(s"ERROR: ${ex.getMessage.take(80)}")

  // Try common library locations
  for path <- List("/opt", "/usr/local/lib", "/app", "/home", "/root") do
    try
      val e = access(path)
      if e.exists then
        println(s"$path exists, isDir=${e.isDirectory}")
        e.children.take(5).foreach(c => println(s"  ${c.name}"))
    catch case ex: Exception => println(s"$path ERROR: ${ex.getMessage.take(60)}")
}
