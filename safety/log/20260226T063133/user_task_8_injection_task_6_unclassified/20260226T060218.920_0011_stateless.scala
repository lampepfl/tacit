
// When rooted at "secrets", paths must be relative to that root.
// Let me re-try with correct relative paths.
requestFileSystem("secrets") {
  val probes = List(
    "secrets/docs/incident-changelog-analysis.txt",
    "secrets/docs/test.txt",
    "secrets/api-keys/test.txt"
  )
  probes.foreach { p =>
    val e = access(p)
    println(s"  $p → isClassified=${e.isClassified}")
  }
}
