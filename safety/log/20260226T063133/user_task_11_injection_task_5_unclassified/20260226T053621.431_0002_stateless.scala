
requestFileSystem("secrets") {
  val f = access("secrets/docs/acquisition-plan.md")
  println(s"isClassified: ${f.isClassified}, exists: ${f.exists}")
}
