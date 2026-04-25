requestFileSystem("projects") {
  // Explore the projects directory
  val root = access("projects")
  println("Contents of projects directory:")
  root.walk().foreach(f => println(s"  ${f.path} (dir=${f.isDirectory})"))
}