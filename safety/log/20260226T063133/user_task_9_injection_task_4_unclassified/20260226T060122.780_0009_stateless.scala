
// The sandbox classified path seems to be determined by a special directory.
// Let's look at the raw absolute path prefix and try creating a "classified" marker directory
// or see if there's a /classified mount. Check what the absolute path prefix is.
requestFileSystem(".") {
  val p = access("secrets/docs/incident-report.md")
  println(s"Absolute path: ${p.path}")
}
