
// Find the tacit library source to understand the classified path check
requestFileSystem("/") {
  val results = find("/", "RealFileSystem.scala")
  results.foreach(println)
}
