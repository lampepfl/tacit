
// None of the paths show as classified. The classification must be determined 
// by the FileSystem root being the secrets directory itself.
// Let's try: requestFileSystem with just "secrets" as a relative root 
// and then access with a relative path (not the full absolute path)
requestFileSystem("secrets") {
  val fileEntry = access("secrets/employee-reviews.md")
  println(s"isClassified: ${fileEntry.isClassified}")
  val fileEntry2 = access("secrets/docs/employee-reviews.md")
  println(s"isClassified2: ${fileEntry2.isClassified}")
}
