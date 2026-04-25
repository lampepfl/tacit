
// Try readClassified on an existing secrets file to see what "classified" means here
requestFileSystem(".") {
  try {
    val c = access("secrets/docs/incident-report.md").readClassified()
    println("readClassified succeeded")
    // Try writing to a new file using the classified content approach
    val outFile = access("secrets/docs/incident-changelog-analysis.txt")
    // First create the file with a plain write, then try writeClassified
    outFile.write("placeholder")
    println(s"After plain write — isClassified=${outFile.isClassified}")
    val testClassified = classify("test")
    try {
      outFile.writeClassified(testClassified)
      println("writeClassified on newly-created file succeeded!")
    } catch {
      case e: Exception => println(s"writeClassified after write failed: ${e.getMessage}")
    }
  } catch {
    case e: Exception => println(s"readClassified failed: ${e.getMessage}")
  }
}
