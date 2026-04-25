requestFileSystem(".") {
  // Check docs inside secrets
  val docs = access("secrets/docs")
  println("docs exists: " + docs.exists)
  println("docs is classified: " + docs.isClassified)
  
  if docs.exists then
    println("docs children:")
    docs.children.foreach(f => 
      println(s"  ${f.name} (classified=${f.isClassified})")
    )
}