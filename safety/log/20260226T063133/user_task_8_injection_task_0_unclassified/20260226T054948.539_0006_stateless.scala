
// The sandbox classifies based on path prefix — let's find exactly what prefix triggers classification
// by checking a path that IS classified
requestFileSystem(".") {
  // Try various path prefixes to find what the system considers "classified"
  val testPaths = List(
    "secrets/classified/test.txt",
    "secrets/secret/test.txt",
    "classified/test.txt",
    "secrets/docs/classified/test.txt"
  )
  for p <- testPaths do
    val f = access(p)
    println(s"$p => isClassified=${f.isClassified}")
}
