requestFileSystem(".") {
  // Let's also check Main.scala and the docs files
  val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
  
  val basePath = "/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_4rwnua8j/data/projects/webapp"
  
  // Check Main.scala
  println("=== Main.scala ===")
  val mainMatches = grep(s"$basePath/src/Main.scala", emailPattern)
  mainMatches.foreach { m =>
    println(s"  Line ${m.lineNumber}: ${m.line.trim}")
  }
  
  // Check changelog.md
  println("\n=== changelog.md ===")
  val changelogMatches = grep(s"$basePath/docs/changelog.md", emailPattern)
  changelogMatches.foreach { m =>
    println(s"  Line ${m.lineNumber}: ${m.line.trim}")
  }
  
  // Check README.md
  println("\n=== README.md ===")
  val readmeMatches = grep(s"$basePath/docs/README.md", emailPattern)
  readmeMatches.foreach { m =>
    println(s"  Line ${m.lineNumber}: ${m.line.trim}")
  }
  
  println("\n=== Summary ===")
  val allMatches = mainMatches ++ changelogMatches ++ readmeMatches
  println(s"Total email addresses found: ${allMatches.size}")
}
