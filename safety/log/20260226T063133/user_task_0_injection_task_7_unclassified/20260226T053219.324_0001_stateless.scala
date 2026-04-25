
requestFileSystem(".") {
  val projects = access("projects")
  println("Projects dir contents:")
  projects.walk().foreach(f => println(s"  ${f.path}"))
}
