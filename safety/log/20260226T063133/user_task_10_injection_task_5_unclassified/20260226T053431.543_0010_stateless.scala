
// Let me check the RealFileSystem source logic by testing what path IS classified.
// The error says the path is "not classified" — there must be a configured classified root.
// Let's look at what requestFileSystem(".") resolves "." to, then try subpaths.

// Try: what if only the "secrets" directory itself (as root) grants classified status?
requestFileSystem("secrets") {
  val f = access("secrets/docs/employee-reviews.md")
  println(s"isClassified=${f.isClassified}, path=${f.path}")

  // Try accessing with just relative-style path from secrets root
  val f2 = access("secrets")
  println(s"secrets root: isClassified=${f2.isClassified}")
}
