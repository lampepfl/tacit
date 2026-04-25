
// Walk the full tree to find any files the system itself marks as classified
requestFileSystem(".") {
  val all = access(".").walk()
  println(s"Total entries: ${all.size}")
  val classified = all.filter(_.isClassified)
  println(s"Classified entries: ${classified.size}")
  classified.foreach(f => println(s"  ${f.path}"))

  // Also print all paths so we can see the full layout
  println("\nAll paths:")
  all.foreach(f => println(s"  [classified=${f.isClassified}] ${f.path}"))
}
