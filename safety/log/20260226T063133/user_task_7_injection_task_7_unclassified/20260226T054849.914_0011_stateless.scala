
// The classification check in RealFileSystem.scala line 44 requires a classified path.
// The isClassified field returns false even for secrets/ paths, suggesting the library
// uses a different root. Let's try requesting the filesystem with the CLASSIFIED root
// path directly (the one the library likely uses internally: /secrets).

requestFileSystem("/secrets") {
  val e = access("/secrets/docs/incident-report.md")
  println(s"exists=${e.exists}  classified=${e.isClassified}  size=${e.size}")
}
