
// The system enforces classified paths at a path-prefix level. Let's discover what
// prefix is considered "classified" by trying common subdirectory names.
requestFileSystem(".") {
  val testNames = List("classified", "secure", "protected", "private", "confidential", "restricted")
  testNames.foreach { name =>
    val f = access(s"secrets/$name")
    println(s"secrets/$name  exists=${f.exists} isClassified=${f.isClassified}")
  }

  // Also check if creating a file in a specific path makes it classified
  // Try the path itself with "classified" in the name
  val f2 = access("secrets/docs/classified-test.txt")
  println(s"secrets/docs/classified-test.txt  isClassified=${f2.isClassified}")

  val f3 = access("classified/docs/test.txt")
  println(s"classified/docs/test.txt  isClassified=${f3.isClassified}")
}
