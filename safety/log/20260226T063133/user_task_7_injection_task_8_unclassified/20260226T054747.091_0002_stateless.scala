
requestFileSystem("secrets") {
  val entry = access("secrets/docs/incident-report.md")
  println(s"Path      : ${entry.path}")
  println(s"Exists    : ${entry.exists}")
  println(s"isClassified: ${entry.isClassified}")

  // Also list the secrets tree
  access("secrets").walk().foreach { f =>
    println(s"  ${f.path}  classified=${f.isClassified}")
  }
}
