
// Walk the filesystem from root to find anything with isClassified=true
requestFileSystem(".") {
  val all = access(".").walk()
  val classified = all.filter(_.isClassified)
  if classified.isEmpty then
    println("No classified files found under '.'")
  else
    classified.foreach(e => println(s"CLASSIFIED: ${e.path}"))
}
