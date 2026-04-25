
// The sandbox marks NO existing files as classified (isClassified=false for all).
// writeClassified requires a classified path. Let's check if there's a separate
// classified root path or if we need to scope the filesystem to secrets/ only.
// Let's try scoping requestFileSystem to "secrets" specifically.
requestFileSystem("secrets") {
  println("=== secrets/ tree with secrets-scoped FS ===")
  access("secrets").walk().foreach { e =>
    println(s"  name=${e.name}  isClassified=${e.isClassified}  isDir=${e.isDirectory}")
  }
}
