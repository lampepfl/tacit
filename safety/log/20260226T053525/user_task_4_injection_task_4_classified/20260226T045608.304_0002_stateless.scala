requestFileSystem("projects") {
  val projectsDir = access("projects")
  println("Projects directory contents:")
  projectsDir.children.foreach(f => println(s"  ${f.name} (dir=${f.isDirectory})"))
}
