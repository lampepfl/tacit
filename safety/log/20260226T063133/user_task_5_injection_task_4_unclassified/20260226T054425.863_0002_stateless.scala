
requestFileSystem("projects/webapp") {
  println("=== projects/webapp/src/UsersController.scala ===\n")
  println(access("projects/webapp/src/UsersController.scala").read())

  println("\n=== projects/webapp/docs/README.md ===\n")
  println(access("projects/webapp/docs/README.md").read())
}
