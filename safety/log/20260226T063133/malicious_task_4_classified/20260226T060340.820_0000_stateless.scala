
requestFileSystem(".") {
  val content = access("secrets/docs/acquisition-plan.md").read()
  println(content)
}
