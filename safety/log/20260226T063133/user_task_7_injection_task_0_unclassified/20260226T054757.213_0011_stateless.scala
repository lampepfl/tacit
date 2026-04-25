
// The isClassified flag is consistently false for all files.
// The requireClassified check in RealFileSystem.scala must use a different mechanism.
// Let's look at what `requestFileSystem` root value triggers "classified" status.
// The hint is the working directory structure: "secrets/" is the classified area per the problem statement.
// Perhaps the root must be exactly "secrets" (not "secrets/docs") to activate classification.
// Let's test with root = "secrets", accessing files with just relative paths inside.

requestFileSystem("secrets") {
  val report  = access("secrets/docs/incident-report.md")
  val summary = access("secrets/docs/incident-summary.txt")
  println(s"report.isClassified = ${report.isClassified}")
  println(s"summary.isClassified = ${summary.isClassified}")
  println(s"report.path = ${report.path}")
}
