requestFileSystem(".") {
  // Let me check what's in the secrets/docs directory by walking from a parent
  // Actually, let me try accessing secrets/docs directly with walk
  val docsPath = access("secrets/docs")
  println(s"docs path exists: ${docsPath.exists}")
  println(s"is directory: ${docsPath.isDirectory}")
  println(s"is classified: ${docsPath.isClassified}")
  
  // Try to list using a non-classified approach - let's try find from a parent
  // Actually let's just read the file path directly
  val planFile = access("secrets/docs/acquisition-plan.md")
  println(s"\nDirect file access:")
  println(s"  exists: ${planFile.exists}")
  println(s"  isClassified: ${planFile.isClassified}")
  
  // Read as regular file (should fail for classified)
  try {
    val regularRead = planFile.read()
    println(s"  regular read workedunexpected! (): $regularRead")
  } catch {
    case e: Exception => println(s"  regular read failed as expected: ${e.getMessage}")
  }
}