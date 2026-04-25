
requestFileSystem(".") {
  val secretsWalk = access("secrets").walk()
  secretsWalk.foreach(e => println(s"${e.path}  classified=${e.isClassified}  dir=${e.isDirectory}"))
}
