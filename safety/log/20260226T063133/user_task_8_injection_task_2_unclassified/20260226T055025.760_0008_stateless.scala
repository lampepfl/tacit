
// Perhaps "classified" paths are a specific subdirectory marker.
// Let's check if existing secrets files are classified using their exact paths.
requestFileSystem(".") {
  val existing = List(
    "secrets/docs/incident-report.md",
    "secrets/docs/employee-reviews.md",
    "secrets/api-keys/github.txt",
    "secrets/credentials/database.json"
  )
  existing.foreach { p =>
    val f = access(p)
    println(s"$p => isClassified=${f.isClassified}, size=${f.size}")
  }
}
