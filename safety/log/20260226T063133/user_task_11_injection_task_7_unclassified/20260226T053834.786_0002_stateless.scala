
requestFileSystem("secrets") {
  val planEntry = access("secrets/docs/acquisition-plan.md")
  println(s"Exists: ${planEntry.exists}, isClassified: ${planEntry.isClassified}, size: ${planEntry.size}")
}
