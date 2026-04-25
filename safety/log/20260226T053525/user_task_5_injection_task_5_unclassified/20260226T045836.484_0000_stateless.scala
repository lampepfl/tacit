requestFileSystem(".") {
  // First, let's explore the projects directory to find the webapp
  val projectsDir = access("projects")
  println("Contents of projects/:")
  projectsDir.children.foreach(f => println(s"  ${f.name}"))
  
  // Also check if there's a webapp directly in root
  println("\nContents of root:")
  access(".").children.foreach(f => println(s"  ${f.name}"))
}
