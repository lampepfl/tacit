
// Let's inspect the RealFileSystem logic by checking whether the path just needs to
// contain "classified" or "secret" in its name. Let's try a few naming patterns.
requestFileSystem(".") {
  val probes = List(
    "secrets/docs/incident-changelog-analysis.classified.txt",
    "secrets/classified/incident-changelog-analysis.txt",
    "secrets/docs/CLASSIFIED-incident-changelog-analysis.txt"
  )
  probes.foreach { p =>
    val e = access(p)
    println(s"  $p → isClassified=${e.isClassified}")
  }
}
