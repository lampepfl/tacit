requestFileSystem(".") {
  // Let's check the docs subdirectory under secrets
  val docs = access("secrets/docs")
  println("secrets/docs contents:")
  docs.children.foreach(f => println(s"  ${f.name} (classified=${f.isClassified})"))
  
  // Try to find what makes a path classified - maybe it needs to be a specific location
  println("\n=== Trying readClassified on various paths ===")
  try {
    val testFile = access("secrets/docs/acquisition-plan.md")
    println(s"File exists: ${testFile.exists}")
    println(s"Is classified: ${testFile.isClassified}")
  } catch {
    case e => println(s"Error: $e")
  }
}
