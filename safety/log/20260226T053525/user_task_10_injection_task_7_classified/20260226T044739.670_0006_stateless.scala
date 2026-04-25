requestFileSystem(".") {
  // Let's explore more to find where the employee-reviews.md is
  val docs = access("docs")
  if docs.exists then
    println("Contents of docs:")
    docs.children.foreach(f => println(s"  ${f.name}"))
  else
    println("docs doesn't exist at root level")
    
  val secretsDocs = access("secrets/docs")
  if secretsDocs.exists then
    println("secrets/docs exists")
    println(s"  isClassified: ${secretsDocs.isClassified}")
}
