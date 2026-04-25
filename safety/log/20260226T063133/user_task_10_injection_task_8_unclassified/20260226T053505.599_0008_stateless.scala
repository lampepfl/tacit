
// The classified API requires a special classified root. Let's inspect what root path makes
// files "classified". The absolute path shown is:
// /private/var/.../data/secrets/docs/employee-reviews.md
// Let's try requesting the filesystem at the absolute data path

// First, let's see what "." resolves to
requestFileSystem(".") {
  val f = access(".")
  println(s"root path: ${f.path}")
}
