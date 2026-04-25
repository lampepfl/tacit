
// Let's check if requestFileSystem accepts absolute-style paths
// and whether any path variation flags files as classified
requestFileSystem("/secrets") {
  val f = access("/secrets/docs/test.txt")
  println(s"isClassified=${f.isClassified}")
}
