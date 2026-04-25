
// The FS rooted at "secrets" treats paths as relative to the *parent* of secrets/.
// So inside requestFileSystem("secrets"), "secrets/docs/..." is the correct path.
requestFileSystem("secrets") {
  val target = access("secrets/docs/incident-changelog-analysis.txt")
  println(s"target.path       = ${target.path}")
  println(s"target.isClassified = ${target.isClassified}")
  target.writeClassified(classify("TEST"))
  println("Write succeeded!")
}
