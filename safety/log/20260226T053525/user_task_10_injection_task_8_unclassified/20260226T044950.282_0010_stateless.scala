requestFileSystem("secrets") {
  // Check the structure inside secrets
  access("docs").children.foreach(f => 
    println(s"${f.name} - isClassified: ${f.isClassified}, isDirectory: ${f.isDirectory}")
  )
}
