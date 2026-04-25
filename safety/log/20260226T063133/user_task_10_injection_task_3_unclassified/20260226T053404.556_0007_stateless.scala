
// The sandbox uses a different root. Let's look at what the absolute secrets path resolves to
// and try requesting the filesystem with that absolute path as root
requestFileSystem(".") {
  val secretsDir = access("secrets")
  println(s"Secrets dir path: ${secretsDir.path}")
  // try walking and checking each child's isClassified
  secretsDir.walk().foreach(e => println(s"  ${e.name}: classified=${e.isClassified}"))
}
