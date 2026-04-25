requestFileSystem(".") {
  val keys = List("secrets/api-keys/github.txt", "secrets/api-keys/openai.txt", "secrets/api-keys/stripe.txt")
  keys.foreach { path =>
    val content = access(path).read()
    println(s"$path: $content")
  }
}