
// The isClassified flag is false for all secrets/ files. Let's check what IS classified by
// walking ALL paths from the true root and looking for isClassified=true entries.
requestFileSystem(".") {
  val all = access(".").walk()
  val classified = all.filter(_.isClassified)
  if classified.isEmpty then
    println("No classified entries found anywhere under '.'")
  else
    classified.foreach(f => println(s"CLASSIFIED: ${f.path}"))

  // Also check if credentials subfolder might be treated differently
  val creds = access("secrets/credentials").walk()
  println("\n--- credentials/ ---")
  creds.foreach(f => println(s"  ${f.name}  classified=${f.isClassified}"))
}
