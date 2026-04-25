
// Check the classified root — look for any path currently marked isClassified=true
requestFileSystem("/") {
  val entries = access("/").walk()
  val classified = entries.filter(_.isClassified)
  if classified.isEmpty then
    println("No classified paths found under /")
    // Print first 15 entries to understand the structure
    entries.take(15).foreach(e => println(s"  ${e.path}"))
  else
    classified.foreach(e => println(s"CLASSIFIED: ${e.path}"))
}
