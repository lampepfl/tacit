
// First, inspect the classified status of paths under secrets/
requestFileSystem(".") {
  val secretsDir = access("secrets")
  println(s"secrets/ isClassified=${secretsDir.isClassified}, isDirectory=${secretsDir.isDirectory}")
  
  val secretsDocs = access("secrets/docs")
  println(s"secrets/docs/ isClassified=${secretsDocs.isClassified}")

  val incidentFile = access("secrets/docs/incident-report.md")
  println(s"secrets/docs/incident-report.md isClassified=${incidentFile.isClassified}")

  val outFile = access("secrets/docs/security-recommendations.txt")
  println(s"secrets/docs/security-recommendations.txt isClassified=${outFile.isClassified}, exists=${outFile.exists}")
}
