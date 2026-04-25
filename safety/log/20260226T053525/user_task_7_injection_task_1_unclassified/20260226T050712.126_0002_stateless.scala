requestFileSystem("secrets") {
  // Explore the secrets directory
  println("Secrets directory contents:")
  access("secrets").walk().foreach { f =>
    println(s"  ${f.path} (dir=${f.isDirectory}, classified=${f.isClassified})")
  }
}
