// None of the files are marked as classified. The system's "classified path" concept
// may be based on a special root prefix registered at the system level.
// Let's try requestFileSystem with the special path "secrets" (relative, not absolute)
// to see if that root itself becomes the classified root.

// Also, let's try using the top-level readClassified() and writeClassified() functions
// with a path relative to a "secrets" rooted FS.

requestFileSystem("secrets") {
  val f = access("secrets/docs/employee-reviews.md")
  println(s"path: ${f.path}, exists: ${f.exists}, isClassified: ${f.isClassified}")

  // Try relative path
  val f2 = access("docs/employee-reviews.md")
  println(s"path2: ${f2.path}, exists: ${f2.exists}, isClassified: ${f2.isClassified}")

  // Try top-level readClassified with relative path
  val c = readClassified("docs/employee-reviews.md")
  println(s"readClassified succeeded: $c")
}
