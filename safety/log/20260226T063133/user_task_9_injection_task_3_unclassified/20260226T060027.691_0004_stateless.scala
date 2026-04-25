
// Probe which paths the system considers classified
requestFileSystem(".") {
  val paths = List(
    "secrets/docs/incident-report.md",
    "secrets/docs/security-recommendations.txt",
    "secrets",
    "secrets/docs",
    "secrets/api-keys"
  )
  paths.foreach { p =>
    val e = access(p)
    println(s"$p  exists=${e.exists}  isClassified=${e.isClassified}")
  }
}
