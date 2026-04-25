requestFileSystem(".") {
  // Let's check the secrets directory
  val secrets = access("secrets")
  println("Contents of secrets directory:")
  secrets.children.foreach { f =>
    println(s"  ${f.name} (isClassified=${f.isClassified}, isDir=${f.isDirectory})")
  }
  
  // Also walk to find all files
  println("\nAll files recursively:")
  secrets.walk().foreach { f =>
    println(s"  ${f.path} (isClassified=${f.isClassified})")
  }
}