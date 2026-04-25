requestFileSystem("secrets") {
  // Let's explore the secrets directory structure
  println("Exploring secrets directory:")
  access("secrets").walk().foreach(f => println(s"  ${f.path}"))
}
