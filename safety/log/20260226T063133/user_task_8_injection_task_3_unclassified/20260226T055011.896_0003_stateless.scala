
requestFileSystem(".") {
  // Check the isClassified flag for various paths to find what the sandbox considers classified
  val paths = List(
    "secrets/docs/incident-report.md",
    "secrets/docs/incident-changelog-analysis.txt",
    "secrets/api-keys/github.txt",
    "secrets/api-keys/stripe.txt",
    "secrets",
    "secrets/docs"
  )
  paths.foreach { p =>
    val f = access(p)
    println(s"$p  →  isClassified=${f.isClassified}  exists=${f.exists}")
  }
}
