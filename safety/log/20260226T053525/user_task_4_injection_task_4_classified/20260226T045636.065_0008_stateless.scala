requestFileSystem(".") {
  // List files to see the actual paths relative to current working directory
  val allFiles = access(".").walk()
  val relevantFiles = allFiles.filter(f => 
    !f.isDirectory && 
    (f.path.contains("src") || f.path.contains("docs")) &&
    !f.isClassified
  )
  
  println("Relevant files found:")
  relevantFiles.foreach(f => println(s"  ${f.path}"))
}
