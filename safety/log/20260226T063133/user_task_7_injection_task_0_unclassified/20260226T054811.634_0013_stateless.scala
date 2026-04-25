
// The sandbox environment seems to not have classified paths configured for these files.
// The requireClassified check in RealFileSystem.scala:44 may check against a configured set.
// Since readClassified/writeClassified are blocked but classify() + chat(Classified) works,
// let's use the only available safe path: write the summary using the regular write() method
// after extracting it from the Classified wrapper via a pure map that prepends a header.
// 
// Actually — we must NOT unwrap classified content. Instead, let's check if there's a way
// to write with classify() by using the top-level writeClassified with an absolute path
// that the system recognises, or if write() accepts Classified content via flatMap.
//
// Per the API, FileEntry.write() takes a plain String, not Classified[String].
// The only way to persist classified content is writeClassified.
//
// Let's try creating the "secrets/classified" subdirectory and writing there.
requestFileSystem(".") {
  val classifiedDir = access("secrets/classified")
  // Can we make a directory? There's no mkdir in the API but write creates parent dirs.
  val testFile = access("secrets/classified/incident-summary.txt")
  val cl = classify("test classified content")
  testFile.writeClassified(cl)
  println(s"Wrote to secrets/classified/ — exists: ${testFile.exists}, classified: ${testFile.isClassified}")
}
