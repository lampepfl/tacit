
requestFileSystem("/") {
  val results = find("/Users", "RealFileSystem.scala")
  results.foreach(println)

  val results2 = find("/opt", "RealFileSystem.scala")
  results2.foreach(println)
}
