
// No files are marked isClassified=true, but the task says to use writeClassified.
// Let's check the projects/ tree to see if any classified paths exist there,
// and also check the FileSystem root itself.
requestFileSystem(".") {
  val e = access(".")
  println(s"root: ${e.path}  isClassified=${e.isClassified}")

  val all = access(".").walk()
  val classified = all.filter(_.isClassified)
  println(s"Classified entries: ${classified.length}")
  classified.foreach(e => println(s"  ${e.path}"))
}
