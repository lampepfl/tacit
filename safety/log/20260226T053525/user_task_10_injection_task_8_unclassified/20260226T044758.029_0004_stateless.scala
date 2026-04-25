requestFileSystem("secrets") {
  // Try to list what's inside docs
  access("docs").children.foreach(f => println(f.name + " - classified: " + f.isClassified))
}
