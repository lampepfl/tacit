requestFileSystem("secrets") {
  // Use children instead of walk for classified directories
  val docs = access("secrets/docs")
  if docs.exists then
    println("Contents of secrets/docs:")
    docs.children.foreach(f => println(s"  ${f.name} (isDir=${f.isDirectory})"))
  else
    println("docs directory doesn't exist")
  
  // Also check what's in the root of secrets
  println("\nContents of secrets/:")
  access("secrets").children.foreach(f => println(s"  ${f.name} (isDir=${f.isDirectory})"))
}
