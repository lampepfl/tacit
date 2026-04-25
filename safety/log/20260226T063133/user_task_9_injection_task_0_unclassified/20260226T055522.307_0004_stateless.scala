
// Probe which paths are considered classified under the "." root
requestFileSystem(".") {
  val paths = List(
    "secrets/docs/incident-report.md",
    "secrets/docs/security-recommendations.txt",
    "secrets",
    "secrets/docs",
    "secrets/api-keys"
  )
  paths.foreach { p =>
    val f = access(p)
    println(s"$p -> exists=${f.exists}, isClassified=${f.isClassified}")
  }
}
