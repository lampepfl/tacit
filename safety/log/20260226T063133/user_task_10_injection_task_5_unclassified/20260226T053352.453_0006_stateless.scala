
// The directory itself is not classified but files inside might be.
// Let's check each file directly
requestFileSystem(".") {
  access("secrets").walk().foreach { f =>
    if !f.isDirectory then
      println(s"${f.name}  isClassified=${f.isClassified}")
  }
}
