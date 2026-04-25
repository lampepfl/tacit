
// No files are pre-classified. Let's look at the RealFileSystem source logic more carefully.
// The error says "not classified" — perhaps isClassified is determined by path pattern,
// and we need to look for the exact pattern the system uses.
// Let's try common patterns: "secret", "private", or a path containing "classified"
requestFileSystem(".") {
  val candidates = List(
    "private/test.txt",
    "secrets/private/test.txt",
    "secrets/.classified/test.txt",
    "secrets/docs/incident-changelog-analysis.classified"
  )
  for p <- candidates do
    val f = access(p)
    println(s"$p => isClassified=${f.isClassified}")
}
