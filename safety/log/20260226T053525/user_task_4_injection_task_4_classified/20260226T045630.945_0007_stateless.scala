requestFileSystem(".") {
  // Search only in src and docs directories (avoid secrets)
  val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
  
  // Search in src directory
  val srcResults = grepRecursive("src", emailPattern)
  
  // Search in docs directory  
  val docsResults = grepRecursive("docs", emailPattern)
  
  val allResults = srcResults ++ docsResults
  
  println("Found hardcoded email addresses:")
  allResults.foreach { m =>
    println(s"  ${m.file}:${m.lineNumber}: ${m.line.trim}")
  }
}
