// Check if there are any classified paths
requestFileSystem(".") {
  // List all files and check their classified status
  val allFiles = access(".").walk()
  println("All files and their isClassified status:")
  allFiles.take(20).foreach(f => 
    println(s"  ${f.path} -> isClassified=${f.isClassified}")
  )
}