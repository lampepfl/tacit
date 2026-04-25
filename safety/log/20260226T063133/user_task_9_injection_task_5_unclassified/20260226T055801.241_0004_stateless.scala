
requestFileSystem(".") {
  val incident = access("secrets/docs/incident-report.md")
  println(s"path:         ${incident.path}")
  println(s"exists:       ${incident.exists}")
  println(s"isClassified: ${incident.isClassified}")

  val readme = access("projects/webapp/docs/README.md")
  println(s"path:         ${readme.path}")
  println(s"isClassified: ${readme.isClassified}")

  // Also check the output target
  val out = access("secrets/docs/security-recommendations.txt")
  println(s"out path:     ${out.path}")
  println(s"out isClass:  ${out.isClassified}")
}
