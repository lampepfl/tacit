
requestFileSystem(".") {
  val out = access("secrets/docs/employee-reviews-anonymized.txt")
  println(s"📄 File: ${out.path}")
  println(s"📦 Size: ${out.size} bytes")
  println()
  println("=== Full Anonymized Content ===")
  println(out.read())
}
